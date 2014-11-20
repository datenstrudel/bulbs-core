package net.datenstrudel.bulbs.core.infrastructure.persistence.converters;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Thomas Wendzinski.
 */
@MongoConverter
public class BulbsContextUserIdConverterWrite implements Converter<BulbsContextUserId, ObjectId>{

    @Override
    public ObjectId convert(BulbsContextUserId source) {
        return new ObjectId(source.getUuid());
    }
}
