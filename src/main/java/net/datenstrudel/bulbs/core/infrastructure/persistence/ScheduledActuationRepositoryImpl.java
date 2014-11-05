package net.datenstrudel.bulbs.core.infrastructure.persistence;

import com.google.common.collect.Sets;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepository;
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
@Component(value="scheduledActuationRepository")
public class ScheduledActuationRepositoryImpl implements ScheduledActuationRepository {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationRepositoryImpl.class);
    //~ Construction ///////////////////////////////////////////////////////////
    private final MongoTemplate mongo;
    
    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public ScheduledActuationRepositoryImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }
    @PostConstruct
    public void init(){
        log.info("|- INIT ScheduledActuationRepositoryImpl..");
        log.info(" - DB used: " + mongo.getDb().getName());
        
        IndexOperations iOps = mongo.indexOps(ScheduledActuation.class);
        
        iOps.ensureIndex(new Index()
                .on("schedulerUuid", Sort.Direction.ASC)
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
    @Override
    public ScheduledActuationId nextIdentity(BulbsContextUserId creator) {
        return new ScheduledActuationId(
                "SCHED_ACT-" + UUID.randomUUID().toString().toUpperCase(), creator);
    }

    @Override
    public ScheduledActuation loadById(ScheduledActuationId id) {
        return mongo.findOne(Query.query(Criteria
                .where("schedulerUuid")
                .is(id.getUuid())
                .and("userId").is(id.getUserId().getUuid()))
            , ScheduledActuation.class);
    }
    @Override
    public ScheduledActuation loadByName(BulbsContextUserId userId, String schedulerName) {
        return mongo.findOne(Query.query(Criteria
                .where("userId")
                .is(userId.getUuid())
                .and("name").is(schedulerName))
            , ScheduledActuation.class);
    }
    @Override
    public Set<ScheduledActuation> loadByOwner(BulbsContextUserId userId) {
        return Sets.newHashSet(
                mongo.find(Query.query(Criteria
                .where("userId")
                .is(userId.getUuid()))
            , ScheduledActuation.class) );
    }

    @Override
    public void store(ScheduledActuation scheduler) {
        mongo.save(scheduler);
    }
    @Override
    public void remove(ScheduledActuationId id) {
        mongo.remove(
            new Query(
                Criteria.where("schedulerUuid").is(id.getUuid())
                .and("userId").is(id.getUserId().getUuid())
            ), 
            ScheduledActuation.class);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
