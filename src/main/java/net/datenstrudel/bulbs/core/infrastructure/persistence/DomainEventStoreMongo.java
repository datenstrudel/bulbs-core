package net.datenstrudel.bulbs.core.infrastructure.persistence;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value = "domainEventStore")
public class DomainEventStoreMongo implements DomainEventStore{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(DomainEventStoreMongo.class);
    @Autowired
    private MongoTemplate mongo;
    @Autowired
    private Gson objectSerializer;
    
    private String COLL_NAME;
    @Value("${mongo.bulbs.core.eventstore.size}") int EVT_STORE_SIZE;
    @Value("${mongo.bulbs.core.eventstore.count}") int EVT_STORE_COUNT;
    
    //~ Construction ///////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        IndexOperations iOps = mongo.indexOps(StoredEvent.class);
        log.info("Preparing stored EventStore Mongo JS..");
        if(!mongo.collectionExists(StoredEvent.class) ){
            DBCollection c = mongo.createCollection(
                    StoredEvent.class,
                    new CollectionOptions(EVT_STORE_SIZE, EVT_STORE_COUNT, Boolean.TRUE)
                    );
            COLL_NAME = c.getName();
        }else{
            COLL_NAME = mongo.getCollectionName(StoredEvent.class);
        }
        iOps.ensureIndex(new Index()
                .on("_id", Order.ASCENDING));
        DBCollection coll_Counters = mongo.getDb().getCollection("counters");
        DBObject eventCollCounter = coll_Counters.findOne(new BasicDBObject("_id", COLL_NAME));
        if(eventCollCounter == null){ 
            // We create a new counter, that actually holds the state of the last ID requested
            CommandResult counterCollRes = mongo.getDb().doEval(
                    "db.counters.insert(" +
                    "   {" +
//                    "      _id: '" + COLL_NAME + "'," +
                    "      collTarget: '" + COLL_NAME + "'," +
                    "      seq: 0" +
                    "   }" +
                    ")"
                    );
            counterCollRes.throwOnError();
        }
        CommandResult nextSeqFnRes = mongo.getDb().doEval(
                    "db.system.js.save(" +
                    "   {" +
                    "     _id : 'getNextSequence' ," +
                    "     value : " +
                    "       function getNextSequence(name) { " +
                    "           var oldVal = db.counters.findOne( {collTarget : 'storedEvent'}, {seq:1}).seq; " +
                    "           var ret = db.counters.findAndModify(" + 
                    "           {" + 
                    "               query: { collTarget: name },"+ 
                    "               update: { $set : { seq: oldVal + 1} },"+ 
                    "               'new' : true" + 
                    "           });" +
                    "           return ret.seq;" +
                    "       }" +
                          // Increment ($inc) doesn't work on Mongopi version  
//                            "function getNextSequence(name) {" +
//                            "   var ret = db.counters.findAndModify(" +
//                            "          {" +
//                            "            query: { collTarget: name }," +
//                            "            update: { $inc: { seq: 1 } }," +
//                            "            new: true" +
//                            "          }" +
//                            "   );" +
//                            "   return ret.seq;" +
//                            "}" +
                    "   }" +
                    ");"
                );
        nextSeqFnRes.throwOnError();
        log.info(nextSeqFnRes.toString());
        
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public Long nextIdentity(){
        return ( (Double) mongo.getDb().eval("getNextSequence('"+COLL_NAME+"')")).longValue();
//        return mongo.getDb().doEval("getNextSequence('"+COLL_NAME+"')").getLong("res");
    }
    @Override
    public StoredEvent loadById(Long id) {
        return mongo.findOne(Query.query(Criteria.where("_id").is(id)), StoredEvent.class);
    }
    @Override
    public List<StoredEvent> loadAllStoredEventsSince(long mostRecentPublishedStoredEventId) {
        return mongo.find(
                Query.query(Criteria.where("_id").gt(mostRecentPublishedStoredEventId)), 
                StoredEvent.class);
    }
    
    @Override
    public Long store(DomainEvent event) {
        String event_ser = objectSerializer.toJson(event, event.getClass());
        StoredEvent storedEvent = new StoredEvent(
                nextIdentity(),
                event_ser, 
                event.getOccurredOn(), 
                event.getClass().getName());
        
        mongo.save(storedEvent);
        return storedEvent.getId();
    }
    
    @Override
    public DomainEvent fromStoredEvent(StoredEvent in){
        DomainEvent res;
        try {
            res = (DomainEvent) objectSerializer.fromJson(
                in.getEventBody(), 
                Class.forName(in.getTypeName()));
            return res;
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                    "Couldn' Deserialize unknown DomainEventType["+in.getTypeName()+"]", ex);
        }
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
