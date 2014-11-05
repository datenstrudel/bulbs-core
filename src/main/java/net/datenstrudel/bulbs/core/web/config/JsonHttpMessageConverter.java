package net.datenstrudel.bulbs.core.web.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.datenstrudel.bulbs.core.application.facade.json.DtoJsonConverterFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class JsonHttpMessageConverter extends AbstractHttpMessageConverter<Object>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Gson CONVERTER = DtoJsonConverterFactory.dtoJsonConverter();
    //~ Construction ///////////////////////////////////////////////////////////
    public JsonHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected boolean supports(Class<?> clazz) {
        // This constraint needs to be made in order that serialization doesn't interfere with Swagger's Jackson
        // FIXME: Switch to Jackson Serialization!
        return clazz.getCanonicalName().startsWith("net.datenstrudel");
    }
    @Override
    protected Object readInternal(
            Class<? extends Object> clazz, 
            HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
       
        String msg = IOUtils.toString(inputMessage.getBody());
        try{
            return CONVERTER.fromJson(msg, clazz);
        }catch(JsonSyntaxException jsex){
            throw new HttpMessageNotReadableException(
                    "Couldn't read input message["+msg+"]", jsex);
        }
    }
    @Override
    protected void writeInternal(Object output, HttpOutputMessage outputMessage) 
            throws IOException, HttpMessageNotWritableException {
        
        IOUtils.write(
                CONVERTER.toJson(output), 
                outputMessage.getBody());
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
