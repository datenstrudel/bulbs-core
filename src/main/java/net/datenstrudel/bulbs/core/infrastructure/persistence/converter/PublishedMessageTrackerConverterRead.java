package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

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
public class PublishedMessageTrackerConverterRead 
        implements Converter<DBObject, PublishedMessageTracker> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public PublishedMessageTracker convert(DBObject source) {
        return new PublishedMessageTracker(
                ((ObjectId) source.get("_id")).toString(),
                (String) source.get("type"),
                (Long) source.get("mostRecentPublishedStoredEventId")
        );
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
