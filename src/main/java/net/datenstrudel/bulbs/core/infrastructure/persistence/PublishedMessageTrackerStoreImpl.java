package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value = "publishedMessageTrackerStore")
public class PublishedMessageTrackerStoreImpl implements PublishedMessageTrackerStore{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(PublishedMessageTrackerStoreImpl.class);
    @Autowired
    private MongoTemplate mongo;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    
    @Override
    public void store(PublishedMessageTracker msgTracker) {
        mongo.save(msgTracker);
    }

    @Override
    public PublishedMessageTracker loadByType(String type) {
        return mongo.findOne(
                Query.query(Criteria.where("type").is(type)), 
                PublishedMessageTracker.class);
    }

    

}
