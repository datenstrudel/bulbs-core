package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class BulbsContextUserConverterWrite 
        implements Converter<BulbsContextUser, DBObject> {

    @Override
    public DBObject convert(BulbsContextUser source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getBulbsContextUserlId() != null 
                ? new ObjectId(source.getBulbsContextUserlId().getUuid()) 
                : new ObjectId() );
        dbo.put("email", source.getEmail());
        dbo.put("apiKey", source.getApiKey());
        dbo.put("credentials", source.getCredentials());
        dbo.put("nickname", source.getNickname());
        dbo.put("bulbsPrincipals", mapBulbPrincipalsSet(source.getBulbsPrincipals()));
        return dbo;
    }
    
    private DBObject mapBulbPrincipals(Map<AppId, Set<BulbsPrincipal>> principals){
        BasicDBObject res = new BasicDBObject();
        
        for (Entry<AppId, Set<BulbsPrincipal>> el : principals.entrySet()) {
            res.put(
                    el.getKey().getUniqueAppName(), 
                    mapBulbPrincipalsSet(el.getValue()));
        }
        return res;
    }
            
    private DBObject mapBulbPrincipalsSet(Set<BulbsPrincipal> principals){
        BasicDBList res = new BasicDBList() ;
        for (final BulbsPrincipal el : principals) {
            res.add(new BasicDBObject(
                new HashMap<String, String>(){{
                    put("username", el.getUsername());
                    put("bulbBridgeId", el.getBulbBridgeId());
                    put("appType", el.getAppId().getUniqueAppName());
                    put("state", el.getState().name());
                }}));
        }
        return res;
    }

}
