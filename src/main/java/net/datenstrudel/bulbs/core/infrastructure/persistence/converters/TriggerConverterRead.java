package net.datenstrudel.bulbs.core.infrastructure.persistence.converters;

import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;

/**
 * Created by Thomas Wendzinski.
 */
@MongoConverter
public class TriggerConverterRead implements Converter<DBObject, Trigger> {

    @Override
    public Trigger convert(DBObject source) {
        String type = (String) source.get("type");
        String cronExpr = (String) source.get("cronExpr");
        try {
            Class aClass = Class.forName(type);
            return Trigger.fromCronExpression(aClass, cronExpr);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
