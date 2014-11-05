package net.datenstrudel.bulbs.core.infrastructure.persistence;

import com.google.common.collect.Sets;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value="bulbPresetRepository")
public class PresetRepositoryImpl implements PresetRepository{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(PresetRepositoryImpl.class);
    private final MongoTemplate mongo;
    
    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public PresetRepositoryImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }
    @PostConstruct
    public void init(){
        log.info("|- INIT BulbPresetRepositoryImpl..");
        log.info(" - DB used: " + mongo.getDb().getName());
        
        IndexOperations iOps = mongo.indexOps(Preset.class);
        
        iOps.ensureIndex(new Index()
                .on("presetUuid", Sort.Direction.ASC)
                .on("userId", Sort.Direction.ASC)
                .unique(Index.Duplicates.DROP) 
        );
        iOps.ensureIndex(new Index()
                .on("userId", Sort.Direction.ASC));
        iOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .on("userId", Sort.Direction.ASC));
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public PresetId nextIdentity(BulbsContextUserId userId) {
        return new PresetId(UUID.randomUUID().toString().toUpperCase(), userId);
    }
    
    @Override
    public Preset loadById(PresetId presetId) {
        return mongo.findOne(Query.query(Criteria
                .where("presetUuid")
                .is(presetId.getPresetUuid())
                .and("userId").is(presetId.getUserId().getUuid()))
            , Preset.class);
    }
    @Override
    public Preset loadByName(BulbsContextUserId userId, String presetName) {
        return mongo.findOne(Query.query(Criteria
                .where("userId")
                .is(userId.getUuid())
                .and("name").is(presetName))
            , Preset.class);
    }
    @Override
    public Set<Preset> loadByOwner(BulbsContextUserId userId) {
        return Sets.newHashSet(
                mongo.find(Query.query(Criteria
                .where("userId")
                .is(userId.getUuid()))
            , Preset.class) );
    }

    @Override
    public void store(Preset preset) {
        mongo.save(preset);
    }
    @Override
    public void remove(PresetId presetId) {
         mongo.remove(
            new Query(
                Criteria.where("presetUuid").is(presetId.getPresetUuid())
                .and("userId").is(presetId.getUserId().getUuid())
            ), 
            Preset.class);
    }


}
