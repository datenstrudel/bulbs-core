/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class ScheduledActuationRepositoryWrite 
        implements Converter<ScheduledActuation, DBObject> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    protected static final TypeToken<List<AbstractActuatorCmd>> TYPE_TK_ACTCMD = 
            new TypeToken<List<AbstractActuatorCmd>>(){};
    
    @Autowired
    private Gson gson;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(ScheduledActuation source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? new ObjectId(source.getId()) 
                : new ObjectId() );
        dbo.put("schedulerUuid", source.getScheduleId().getUuid());
        dbo.put("userId", source.getScheduleId().getUserId().getUuid());
        
        dbo.put("name", source.getName());
        dbo.put("created", source.getCreated().getTime());
        dbo.put("deleteAfterExec", source.isDeleteAfterExecution() );
        BasicDBObject trigger = null;
        if(source.getTrigger() != null){
            trigger = new BasicDBObject();
            trigger.put("type", source.getTrigger().getClass().getName());
            trigger.put("cronExp", source.getTrigger().toCronExpression().getCronExpression());
            dbo.put("trigger", trigger);
        }
        dbo.put("states", gson.toJson(
                source.getStates(),
                TYPE_TK_ACTCMD.getType()));

        return dbo;
    }

    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
