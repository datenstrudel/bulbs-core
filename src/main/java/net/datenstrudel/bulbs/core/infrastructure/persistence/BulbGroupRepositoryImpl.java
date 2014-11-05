package net.datenstrudel.bulbs.core.infrastructure.persistence;

import com.google.common.collect.Sets;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupRepository;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
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
@Component(value="bulbGroupRepository")
public class BulbGroupRepositoryImpl implements BulbGroupRepository{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbGroupRepositoryImpl.class);
    private final MongoTemplate mongo;

    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public BulbGroupRepositoryImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }
    @PostConstruct
    public void init(){
        log.info("|- INIT BulbGroupRepositoryImpl..");
        log.info(" - DB used: " + mongo.getDb().getName());
        
        IndexOperations iOps = mongo.indexOps(BulbGroup.class);
        
        iOps.ensureIndex(new Index()
                .on("bulbGroupUuid", Sort.Direction.ASC)
                .on("owner", Sort.Direction.ASC)
                .unique(Index.Duplicates.DROP) 
        );
        iOps.ensureIndex(new Index()
                .on("owner", Sort.Direction.ASC));
        iOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .on("owner", Sort.Direction.ASC));
        
    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbGroupId nextIdentity(BulbsContextUserId creatorId) {
        return new BulbGroupId(creatorId, UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public BulbGroup loadById(BulbGroupId groupId) {
        return mongo.findOne(Query.query(Criteria
                .where("bulbGroupUuid")
                .is(groupId.getGroupUuid())
                .and("owner").is(groupId.getUserId().getUuid()))
            , BulbGroup.class);
    }
    @Override
    public BulbGroup loadByName(BulbsContextUserId userId, String groupname) {
        return mongo.findOne(Query.query(Criteria
                .where("owner")
                .is(userId.getUuid())
                .and("name").is(groupname))
            , BulbGroup.class);
    }
    @Override
    public Set<BulbGroup> loadByOwner(BulbsContextUserId userId) {
        return Sets.newHashSet(
                mongo.find(Query.query(Criteria
                .where("owner")
                .is(userId.getUuid()))
            , BulbGroup.class));
    }

    @Override
    public void store(BulbGroup group) {
        mongo.save(group);
    }
    @Override
    public void remove(BulbGroupId groupId) {
        mongo.remove(
            new Query(
                Criteria.where("bulbGroupUuid").is(groupId.getGroupUuid())
                .and("owner").is(groupId.getUserId().getUuid())
            ), 
            BulbGroup.class);
    }

    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
