package net.datenstrudel.bulbs.core.websocket;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.facade.json.DtoJsonConverterFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

/**
 * Converter implementation to transparently handle JSON encoded DTO payload with web sockets.
 * @author Thomas Wendzinski
 */
public class DtoWsMessageJsonAdapter implements MessageConverter {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Gson CONVERTER = DtoJsonConverterFactory.dtoJsonConverter();
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public Object fromMessage(Message<?> message, Class<?> targetClass) {
        return CONVERTER.fromJson( 
                new String( (byte[]) message.getPayload()), 
                targetClass);
    }
    @Override
    public Message<?> toMessage(Object payload, MessageHeaders header) {
        return new GenericMessage(CONVERTER.toJson(payload).getBytes(), header);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
