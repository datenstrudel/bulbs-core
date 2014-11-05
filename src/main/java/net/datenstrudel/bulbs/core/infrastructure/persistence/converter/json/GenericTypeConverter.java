package net.datenstrudel.bulbs.core.infrastructure.persistence.converter.json;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class GenericTypeConverter 
        implements JsonSerializer<Object>, JsonDeserializer<Object> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final String FIELD_NAME__TYPE = "objectType";
    private static final String FIELD_NAME__CONTENT = "objectContent";
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc,
        JsonSerializationContext context) {

        JsonObject res = new JsonObject();
        String type = src.getClass().getCanonicalName();
        res.addProperty(FIELD_NAME__TYPE, type);
        JsonElement elem = context.serialize(src); 
        res.add(FIELD_NAME__CONTENT,  elem);
        return res;
    }

    @Override
    public Object deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException  {
        JsonObject jsonObject =  json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(FIELD_NAME__TYPE);
        String className = prim.getAsString();

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
        return context.deserialize(jsonObject.get(FIELD_NAME__CONTENT), clazz);
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
