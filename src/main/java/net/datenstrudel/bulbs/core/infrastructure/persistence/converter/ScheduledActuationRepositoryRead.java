package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class ScheduledActuationRepositoryRead 
        implements Converter<DBObject, ScheduledActuation>{

    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationRepositoryRead.class);
    @Autowired
    private Gson gson;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public ScheduledActuation convert(DBObject source) {
        ScheduledActuation res = new ScheduledActuation(
                new ScheduledActuationId(
                        (String) source.get("schedulerUuid"), 
                        new BulbsContextUserId( (String) source.get("userId"))),
                (String) source.get("name"),
                (Boolean) source.get("deleteAfterExec"));
        
        Field reflField = ReflectionUtils.findField(ScheduledActuation.class, "id");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(reflField, res, 
                ((ObjectId) source.get("_id")).toString());
        
        reflField = ReflectionUtils.findField(ScheduledActuation.class, "name");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(reflField, res, 
                ( (String) source.get("name")));
        
        reflField = ReflectionUtils.findField(ScheduledActuation.class, "created");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(reflField, res, 
                ( new Date( (Long) source.get("created"))));
                
        //~ Trigger
        DBObject trigger;
        if( source.get("trigger") != null ){
            trigger = (DBObject) source.get("trigger");
            Class<Trigger> type;
            try {
                type = (Class) Class.forName( (String) trigger.get("type"));
                reflField = ReflectionUtils.findField(ScheduledActuation.class, "trigger");
                ReflectionUtils.makeAccessible(reflField);
                ReflectionUtils.setField(reflField, res, 
                        Trigger.fromCronExpression( type, (String) trigger.get("cronExp")));
            } catch (ClassNotFoundException ex) {
                log.error("Trigger couldn't be loaded, due to type unknown: " + trigger.get("type") );
                throw new RuntimeException("Trigger couldn't be loaded, due to type unknown", ex);
            } catch (ParseException pe){
                log.error("Trigger couldn't be loaded, due to parsing error: " + trigger.get("cronExp"), pe );
            }
        }
        
        reflField = ReflectionUtils.findField(ScheduledActuation.class, "states");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(reflField, res, 
                 gson.fromJson( 
                         (String) source.get("states"), 
                         ScheduledActuationRepositoryWrite.TYPE_TK_ACTCMD.getType()) );

        return res;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
