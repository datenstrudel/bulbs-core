package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepositoryExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value="bulbPresetRepository")
public class PresetRepositoryImpl implements PresetRepositoryExt{

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
                .on("_id.presetUuid", Sort.Direction.ASC)
                .on("_id.creator", Sort.Direction.ASC)
                .unique(Index.Duplicates.DROP)
        );
        iOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .on("_id.creator", Sort.Direction.ASC));
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public PresetId nextIdentity(BulbsContextUserId userId) {
        return new PresetId(UUID.randomUUID().toString().toUpperCase(), userId);
    }

}
