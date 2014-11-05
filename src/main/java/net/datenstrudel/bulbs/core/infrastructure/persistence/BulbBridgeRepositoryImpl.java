package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeRepository;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value = "bulbBridgeRepository")
public class BulbBridgeRepositoryImpl implements BulbBridgeRepository {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private MongoTemplate mongo;
    
    //~ Construction ///////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        IndexOperations iOps = mongo.indexOps(BulbBridge.class);
        iOps.ensureIndex(new Index()
                .on("bulbBridgeId", Order.ASCENDING)
                .unique(Index.Duplicates.DROP) );
        iOps.ensureIndex(new Index()
                .on("owner", Order.ASCENDING));
        
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbBridgeId nextIdentity() {
        return new BulbBridgeId(UUID.randomUUID().toString().toUpperCase());
    }
    
    @Override
    public BulbBridge loadById(BulbBridgeId id) {
        return mongo.findOne(Query.query(Criteria
                .where("bulbBridgeId")
                .is(id.getUuId()))
            , BulbBridge.class);
    }
    @Override
    public Set<BulbBridge> loadByOwner(BulbsContextUserId userId) {
        return new HashSet<>(
                mongo.find(
                new Query(
                    Criteria.where("owner").is(userId.getUuid())), 
                BulbBridge.class) );
    }
    public Set<BulbBridge> loadByOwnerAndBulbname(BulbsContextUserId userId, String bulbName) {
        return new HashSet<>(
                mongo.find(
                new Query(
                    Criteria.where("owner").is(userId.getUuid())
                    .and("bulbs.name").is(bulbName)
                ), 
                BulbBridge.class) );
        
    }
    
    @Override
    public void store(BulbBridge bulbBridge) {
        mongo.save(bulbBridge);
    }
    @Override
    public void remove(BulbBridgeId id) {
        mongo.remove(
                new Query(
                    Criteria.where("bulbBridgeId").is(id.getUuId())), 
                BulbBridge.class);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
