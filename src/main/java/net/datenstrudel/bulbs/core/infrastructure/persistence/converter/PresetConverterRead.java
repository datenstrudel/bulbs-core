package net.datenstrudel.bulbs.core.infrastructure.persistence.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class PresetConverterRead implements Converter<DBObject, Preset>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private Gson gson;
    
    protected static final TypeToken<List<AbstractActuatorCmd>> TYPE_TK_ACTCMD = 
            new TypeToken<List<AbstractActuatorCmd>>(){};
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public Preset convert(DBObject source) {
        
        Preset res = new Preset(
                new PresetId(
                        (String) source.get("presetUuid"), 
                        new BulbsContextUserId( (String) source.get("userId")) ), 
                (String) source.get("name"));
        
        Field reflField = ReflectionUtils.findField(Preset.class, "id");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(
                reflField,
                res, 
                ((ObjectId) source.get("_id")).toString()); 
        
        reflField = ReflectionUtils.findField(Preset.class, "states");
        ReflectionUtils.makeAccessible(reflField);
        ReflectionUtils.setField(
                reflField,
                res, 
                gson.fromJson( (String) source.get("states"), TYPE_TK_ACTCMD.getType()) ); 
        
        return res;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
