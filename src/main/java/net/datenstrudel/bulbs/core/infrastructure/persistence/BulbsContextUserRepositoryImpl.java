package net.datenstrudel.bulbs.core.infrastructure.persistence;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @param <T> Type a specific repository is supposed to serve
 * @author Thomas Wendzinski
 */
@Component(value = "bulbsContextUserRepository")
public class BulbsContextUserRepositoryImpl implements BulbsContextUserRepository {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private MongoTemplate mongo;
    //~ Construction ///////////////////////////////////////////////////////////
    
    @PostConstruct
    public void init(){
        IndexOperations iOps = mongo.indexOps(BulbsContextUser.class);
        iOps.ensureIndex(new Index().on("email", Order.ASCENDING).unique(Index.Duplicates.RETAIN));
        iOps.ensureIndex(new Index().on("apiKey", Order.ASCENDING).unique(Index.Duplicates.RETAIN));
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbsContextUserId nextIdentity() {
        return new BulbsContextUserId(new ObjectId().toString());
    }

    @Override
    public BulbsContextUser loadByEmail(String email) {
        return mongo.findOne(new Query(Criteria.where("email").is(email)), BulbsContextUser.class);
    }
    @Override
    public BulbsContextUser loadByApiKey(String apiKey) {
        return mongo.findOne(new Query(Criteria.where("apiKey").is(apiKey)), BulbsContextUser.class);
    }

    @Override
    public BulbsContextUser loadById(BulbsContextUserId id) {
        return mongo.findById(new ObjectId(id.getUuid()), BulbsContextUser.class);
    }

    @Override
    public void remove(BulbsContextUser user) {
        mongo.remove(user);
    }

    @Override
    public void store(BulbsContextUser user) {
        mongo.save(user);
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////


}
