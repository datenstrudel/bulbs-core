/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.application.facade.json;

import com.google.gson.*;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.group.DtoGroupActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPresetActuatorCmd;

import java.lang.reflect.Type;

/**
 *
 * @author Thomas Wendzinski
 */
public class TypeHierarchyAdapterDtoActuatorCmd 
        implements JsonDeserializer<DtoAbstractActuatorCmd>, JsonSerializer<DtoAbstractActuatorCmd> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    
    @Override
    public JsonElement serialize(
            DtoAbstractActuatorCmd src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }

    @Override
    public DtoAbstractActuatorCmd deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject src = json.getAsJsonObject();
        String type = src.get("type").getAsString();
        
        switch(type){
            case "BULB":
                return context.deserialize(json, DtoBulbActuatorCmd.class);
            case "GROUP":
                return context.deserialize(json, DtoGroupActuatorCmd.class);
            case "PRESET":
                return context.deserialize(json, DtoPresetActuatorCmd.class);
            default:
                throw new UnsupportedOperationException("Specific "
                        + "sub type of DtoAbstractActuatorCmd not supported; type was: " + type);
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
