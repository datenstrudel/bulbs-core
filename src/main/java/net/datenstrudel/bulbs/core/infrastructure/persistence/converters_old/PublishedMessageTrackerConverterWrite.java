package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class PublishedMessageTrackerConverterWrite 
        implements Converter<PublishedMessageTracker, DBObject> {


    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(PublishedMessageTracker source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? new ObjectId( source.getId() )
                : new ObjectId() );
        dbo.put("type", source.getType());
        dbo.put("mostRecentPublishedStoredEventId", source.getMostRecentPublishedStoredEventId());
        
        return dbo;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
