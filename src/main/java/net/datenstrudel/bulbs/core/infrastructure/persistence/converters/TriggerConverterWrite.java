package net.datenstrudel.bulbs.core.infrastructure.persistence.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Thomas Wendzinski.
 */
@MongoConverter
public class TriggerConverterWrite implements Converter<Trigger, DBObject> {
    @Override
    public DBObject convert(Trigger source) {
        DBObject dbo = new BasicDBObject();

        dbo.put("type", source.getClass().getCanonicalName());
        dbo.put("cronExpr", source.toCronExpression().getCronExpression());

        return dbo;
    }
}
