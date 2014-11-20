package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter

public class BulbGroupConverterWrite implements Converter<BulbGroup, DBObject>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private Gson gson;
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(BulbGroup source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? new ObjectId(source.getId()): new ObjectId() );
                
        dbo.put("bulbGroupUuid", source.getGroupId().getGroupUuid());
        dbo.put("owner", source.getGroupId().getUserId().getUuid());
        dbo.put("name", source.getName());
        dbo.put("bulbs", mapBulbs(source.getBulbs()));
        return dbo;
    }
    
    private DBObject mapBulbs(Set<BulbId> bulbs){
        BasicDBList res = new BasicDBList();
        BasicDBObject tmpBulb;
        for (BulbId el : bulbs) {
            tmpBulb = new BasicDBObject();
            tmpBulb.append("bridgeUUID", el.getBridgeId().getUuId());
            tmpBulb.append("bulbId", el.getLocalId());
            res.add(tmpBulb);
        }
        return res;
    }

}
