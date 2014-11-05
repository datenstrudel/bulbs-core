package net.datenstrudel.bulbs.core.application.messaging.notification.handling;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class RabbitMqListenerRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(RabbitMqListenerRegistry.class);
    private static final ConcurrentHashMap<RabbitMqExchangeListener, Integer> listeners =
            new ConcurrentHashMap<>();

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public static void registerListener(RabbitMqExchangeListener ch, int retryLatency){
        listeners.put(ch, retryLatency);
    }
    @Scheduled(fixedRate = 60000, initialDelay = 120000)
    public void trigger_recover() {
        if(log.isDebugEnabled())log.debug("Recovery Process triggered.");
        for (RabbitMqExchangeListener el : listeners.keySet()) {
            try{
                el.reconnect4Recover();
            }catch(IllegalStateException ioex){
                log.warn("Couldn' re-establich RMQ channel for queue: " + el.queueName() + " | " + ioex.getMessage());
            }catch(IOException ioex){
                log.error("Message recovery triggering failed", ioex);
            }
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
