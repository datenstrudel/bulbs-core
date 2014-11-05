package net.datenstrudel.bulbs.core.application.messaging.notification.handling.bulb;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbBridgeSynced;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class BulbBridgeSyncedHandler_Websocket extends RabbitMqExchangeListener
        implements ApplicationListener<BrokerAvailabilityEvent>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbStateChangedHandler.class);
    private static final String[] subscribedTo = new String[]{
        BulbBridgeSynced.class.getName()
    };
    private final SimpMessageSendingOperations messagingTemplate;
    @Autowired 
    private Gson gson;
    private volatile boolean brokerAvailable = false;
    
    
    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public BulbBridgeSyncedHandler_Websocket(SimpMessageSendingOperations messagingTemplate) {
        super();
        this.messagingTemplate = messagingTemplate;
    }
    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        this.brokerAvailable = event.isBrokerAvailable();
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected void filteredDispatch(String type, String message) {
        Notification nf = gson.fromJson(message, Notification.class);
        BulbBridgeSynced evt = gson.fromJson(nf.getEventBody(), BulbBridgeSynced.class);
        if(log.isDebugEnabled()){
            log.debug(" -- [ X ] GOING TO SEND SOCKET PUSH for BulbBridgeSynced: " + evt);
        }
        
        if (this.brokerAvailable) {
//            this.messagingTemplate.convertAndSendToUser("b@b.de", "/topic/bulbupdates/", evt);
            this.messagingTemplate.convertAndSend("/topic/bridgeSynced/", evt.getBulbBridgeId());
        }else{
            log.warn(" -- WEBSOCKET BROKER WAS NOT AVAILABLE :(");
        }
    }
    @Override
    protected String topicName() {
        return Exchanges.EXCHANGE_TOPIC__BUBLS_CORE;
    }
    @Override
    protected String[] listensToEvents() {
        return subscribedTo;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////


}
