package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.PriorityCoordinator;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class BulbBridgeConverterWrite implements Converter<BulbBridge, DBObject>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private Gson gson;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(BulbBridge source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? new ObjectId(source.getId()) 
                : new ObjectId() );
        dbo.put("bulbBridgeId", source.getBridgeId().getUuId());
        dbo.put("macAddress", source.getMacAddress());
        dbo.put("name", source.getName());
        dbo.put("online", source.isOnline());
        dbo.put("localAddress", source.getLocalAddress().getHost() + ":" + source.getLocalAddress().getPort());
        dbo.put("owner", source.getOwner().getUuid());
        dbo.put("platform", source.getPlatform().name());
        dbo.put("bridgeAttributes", new BasicDBObject(source.getBridgeAttributes()) );
        dbo.put("bulbs", mapBulbs(source.getBulbs()) );
        return dbo;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private DBObject mapBulbs(Set<Bulb> bulbs){
        BasicDBList res = new BasicDBList();
        for (final Bulb el : bulbs) {
            res.add(new BasicDBObject(
                    new HashMap<String, Object>(){{
                        put("bulbId", el.getBulbId().getLocalId() );
                        put("online", el.getOnline());
                        put("name", el.getName());
                        put("bulbAttributes", new BasicDBObject( el.getBulbAttributes()) ) ;
                        put("state", mapBulbState(el.getState() ) );
                        put("priorityCoordinator", mapPriorityCoordinator(el.getPriorityCoordinator()) );
                    }})
            );
        }
        return res;
        
    }

    private DBObject mapBulbState(BulbState state){
        DBObject bulbState = new BasicDBObject();
        bulbState.put("enabled", state.getEnabled());
        bulbState.put("color", gson.toJson( state.getColor(), new TypeToken<Color>(){}.getType()) );
        bulbState.put("transitionDelay", state.getTransitionDelay());
        return bulbState;
    }
    private DBObject mapPriorityCoordinator(PriorityCoordinator in){
        DBObject res = new BasicDBObject();
        res.put("lastActor", in.getLastActor() != null ? in.getLastActor().getUniqueAppName() : null);
        res.put("suspendedActor", in.getSuspendedActor() != null ? in.getSuspendedActor().getUniqueAppName() : null);
        res.put("lastOverrideTime", in.getLastOverrideTime());
        res.put("overrideDurationMillis", in.getOverrideDurationMillis());
        return res;
    }
    
}
