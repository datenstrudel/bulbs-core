package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Thomas Wendzinski.
 */
@Component("bulbsContextUserRepository")
public class BulbsContextUserRepositoryImpl implements BulbsContextUserRepositoryExtension{

    @Autowired
    private MongoTemplate mongo;

    @Override
    @PostConstruct
    public void initIndices() {
        IndexOperations iOps = mongo.indexOps(BulbsContextUser.class);
        iOps.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique(Index.Duplicates.RETAIN));
        iOps.ensureIndex(new Index().on("apiKey", Sort.Direction.ASC).unique(Index.Duplicates.RETAIN));
    }
    @Override
    public BulbsContextUserId nextIdentity() {
        return new BulbsContextUserId(new ObjectId().toString());
    }

}
