package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class StoredEventConverterRead implements Converter<DBObject, StoredEvent>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public StoredEvent convert(DBObject source) {
        return new StoredEvent(
                (Long) source.get("_id"),
                (String) source.get("eventBody"), 
                (Date) source.get("occuredOn"),
                (String) source.get("typeName")
        );
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
