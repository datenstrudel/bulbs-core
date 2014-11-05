package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class BulbGroupConverterRead implements Converter<DBObject, BulbGroup>{

    @Override
    public BulbGroup convert(DBObject source) {
        BulbGroup res = new BulbGroup(
                new BulbGroupId(
                        new BulbsContextUserId( (String) source.get("owner")), 
                        (String) source.get("bulbGroupUuid")), 
                (String) source.get("name"));
        
        Field idField = ReflectionUtils.findField(BulbGroup.class, "id");
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(
                idField,
                res, 
                ((ObjectId) source.get("_id")).toString()); 
        
        idField = ReflectionUtils.findField(BulbGroup.class, "bulbs");
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(
                idField,
                res, 
                mapBulbs((BasicDBList) source.get("bulbs"))); 
        
        return res;
    }
    private Set<BulbId> mapBulbs(BasicDBList source){
        Set<BulbId> res = new HashSet<>();
        Iterator<DBObject> it = (Iterator) source.iterator();
        DBObject sourceBulb;
        while(it.hasNext()){
            sourceBulb = it.next();
            res.add(new BulbId(
                    new BulbBridgeId( (String) sourceBulb.get("bridgeUUID") ), 
                    (Integer) sourceBulb.get("bulbId"))
            );
        }
        return res;
    }

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
