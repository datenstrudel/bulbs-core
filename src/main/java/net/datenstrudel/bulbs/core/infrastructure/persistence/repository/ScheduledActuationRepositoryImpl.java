package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepositoryExt;
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
@Component(value="scheduledActuationRepository")
public class ScheduledActuationRepositoryImpl implements ScheduledActuationRepositoryExt {

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
                .on("name", Sort.Direction.ASC) );
    }
   
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public ScheduledActuationId nextIdentity(BulbsContextUserId creator) {
        return new ScheduledActuationId(
                "SCHED_ACT-" + UUID.randomUUID().toString().toUpperCase(), creator);
    }

}
