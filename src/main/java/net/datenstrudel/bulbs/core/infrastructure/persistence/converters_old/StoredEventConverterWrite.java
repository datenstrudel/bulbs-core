package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class StoredEventConverterWrite implements Converter<StoredEvent, DBObject>{


    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(StoredEvent source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? source.getId() 
                : null );
        dbo.put("eventBody", source.getEventBody());
        dbo.put("occuredOn", source.getOccuredOn());
        dbo.put("typeName", source.getTypeName());
        return dbo;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
