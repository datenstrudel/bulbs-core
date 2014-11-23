package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Thomas Wendzinski.
 */
@Component
public class BulbBridgeConvertListener extends AbstractMongoEventListener<BulbBridge> {

    @Override
    public void onAfterLoad(DBObject dbo) {
        super.onAfterLoad(dbo);
    }

    @Override
    public void onAfterConvert(DBObject dbo, BulbBridge source) {
        source.postLoad();
    }
}
