package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeRepository;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeRepositoryExt;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
@Repository(value = "bulbBridgeRepository")
public class BulbBridgeRepositoryImpl implements BulbBridgeRepositoryExt {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private MongoTemplate mongo;
    
    //~ Construction ///////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        IndexOperations iOps = mongo.indexOps(BulbBridge.class);
        iOps.ensureIndex(new Index()
                .on("owner", Sort.Direction.ASC));
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbBridgeId nextIdentity() {
        return new BulbBridgeId(UUID.randomUUID().toString().toUpperCase());
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
