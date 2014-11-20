package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class BulbsContextUserConverterRead implements Converter<DBObject, BulbsContextUser>{

    @Override
    public BulbsContextUser convert(DBObject source) {
        BulbsContextUser res = new BulbsContextUser(
                new BulbsContextUserId( source.get("_id").toString() ),
                (String) source.get("email"),
                (String) source.get("credentials"), 
                (String) source.get("nickname"),
                (String) source.get("apiKey")
                );
        Field principalsField = ReflectionUtils.findField(BulbsContextUser.class, "bulbsPrincipals");
        ReflectionUtils.makeAccessible(principalsField);
        ReflectionUtils.setField(
                principalsField,
                res, 
                mapPrincipalSet( (BasicDBList)source.get("bulbsPrincipals") ));
        return res;    
    }
    private Set<BulbsPrincipal> mapPrincipalSet(BasicDBList principals){
        Set<BulbsPrincipal> res = new HashSet<>(principals.keySet().size());
        DBObject tmpPr;
        for (Object el : principals) {
            tmpPr = (DBObject) el;
            res.add(new BulbsPrincipal(
                    (String) tmpPr.get("username"),
                    new AppId( (String) tmpPr.get("appType")), 
                    (String) tmpPr.get("bulbBridgeId"), 
                    BulbsPrincipalState.valueOf( (String) tmpPr.get("state")))
            );
        }
        return res;
    }

}
