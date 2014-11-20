package net.datenstrudel.bulbs.core.infrastructure.persistence.converters;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbsContextUserIdConverterRead implements Converter<ObjectId, BulbsContextUserId>{

    @Override
    public BulbsContextUserId convert(ObjectId source) {
        return new BulbsContextUserId( source.toString() );
    }
}
