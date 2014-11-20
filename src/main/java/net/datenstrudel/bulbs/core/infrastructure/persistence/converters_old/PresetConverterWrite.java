package net.datenstrudel.bulbs.core.infrastructure.persistence.converters_old;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Thomas Wendzinski
 */
@MongoConverter
public class PresetConverterWrite implements Converter<Preset, DBObject>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    private Gson gson;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DBObject convert(Preset source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", source.getId() != null 
                ? new ObjectId(source.getId()) 
                : new ObjectId() );
        dbo.put("presetUuid", source.getPresetId().getPresetUuid());
        dbo.put("userId", source.getPresetId().getUserId().getUuid());
        dbo.put("name", source.getName());
        dbo.put("states", gson.toJson(
                source.getStates(), 
                PresetConverterRead.TYPE_TK_ACTCMD.getType()));
        
        return dbo;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
