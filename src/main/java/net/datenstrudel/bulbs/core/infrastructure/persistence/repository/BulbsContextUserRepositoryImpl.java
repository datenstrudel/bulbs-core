package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserRepositoryExt;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * Created by Thomas Wendzinski.
 */
@Repository("bulbsContextUserRepository")
public class BulbsContextUserRepositoryImpl implements BulbsContextUserRepositoryExt{

    @Autowired
    private MongoTemplate mongo;
    public static final Logger log = LoggerFactory.getLogger(BulbsContextUserRepositoryImpl.class);

    @PostConstruct
    public void initIndices() {
        log.info("Init BulbsContextUserRepositoryImpl..");
        IndexOperations iOps = mongo.indexOps(BulbsContextUser.class);
        iOps.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique(Index.Duplicates.RETAIN));
        iOps.ensureIndex(new Index().on("apiKey", Sort.Direction.ASC).unique(Index.Duplicates.RETAIN));
    }
    @Override
    public BulbsContextUserId nextIdentity() {
        return new BulbsContextUserId(new ObjectId().toString());
    }

}
