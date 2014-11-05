package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class BulbBridgeConverterRead implements Converter<DBObject, BulbBridge> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private Gson gson;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbBridge convert(DBObject source) {
        String[] localAddress = ((String)source.get("localAddress")).split(":");
        BulbBridge res = new BulbBridge(
                new BulbBridgeId( ( (String) source.get("bulbBridgeId")).toString() ), 
                (String) source.get("macAddress"), 
                BulbsPlatform.valueOf( (String) source.get("platform") ), 
                new BulbBridgeAddress( localAddress[0], Integer.valueOf(localAddress[1])), 
                (String) source.get("name"), 
                new BulbsContextUserId( (String) source.get("owner") ), 
                new HashMap( ( (DBObject) source.get("bridgeAttributes")).toMap()) );
                
        Field idField = ReflectionUtils.findField(BulbBridge.class, "id");
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(
                idField,
                res, 
                ((ObjectId) source.get("_id")).toString() ); 
        
        Field onlineField = ReflectionUtils.findField(BulbBridge.class, "online");
        ReflectionUtils.makeAccessible(onlineField);
        ReflectionUtils.setField(
                onlineField,
                res, 
                (boolean)source.get("online") );
        //TODO: Further BulbBridge attributes
        Field bulbsField = ReflectionUtils.findField(BulbBridge.class, "bulbs");
        ReflectionUtils.makeAccessible(bulbsField);
        ReflectionUtils.setField(
                bulbsField,
                res, 
                mapBulbs( (BasicDBList)source.get("bulbs"), res) );
        
        return res;
    }
    private Set<Bulb> mapBulbs(BasicDBList bulbs, BulbBridge parentBridge){
        Set<Bulb> res = new HashSet<>(bulbs.size());
        
        DBObject tmpBulb;
        for (Object _el : bulbs ) {
            tmpBulb = (DBObject) _el;
            res.add(new Bulb(
                    new BulbId(parentBridge.getBridgeId(), (Integer) tmpBulb.get("bulbId") ), 
                    parentBridge.getPlatform(),
                    (String) tmpBulb.get("name"), 
                    parentBridge,
                    mapBulbState( (DBObject) tmpBulb.get("state")),
                    (boolean) tmpBulb.get("online"), 
                    new HashMap( ( (DBObject) tmpBulb.get("bulbAttributes")).toMap()),
                    mapPriorityCoordinator( (DBObject) tmpBulb.get("priorityCoordinator"))
            ));
        }
        return res;
    }
    
    private BulbState mapBulbState(DBObject state){
        BulbState res = new BulbState(
                gson.fromJson( (String) state.get("color"), Color.class ), 
                (boolean) state.get("enabled"),
                state.get("transitionDelay") != null ? (Integer) state.get("transitionDelay") : 0
        );
        
        return res;
    }
    
    private PriorityCoordinator mapPriorityCoordinator(DBObject in){
        PriorityCoordinator res = new PriorityCoordinator(
            in.get("lastActor") != null ? new AppId( (String) in.get("lastActor")) : null,
            in.get("suspendedActor") != null ? new AppId( (String) in.get("suspendedActor") ) : null,
            (Long) in.get("lastOverrideTime"),
            (Integer) in.get("overrideDurationMillis")
        );
        return res;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
