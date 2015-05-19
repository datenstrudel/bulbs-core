package net.datenstrudel.bulbs.core.application.messaging.notification.handling.bulb;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.application.services.IBulbBridgeAdminServiceInternal;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbSearchFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class BulbSearchFinishedHandler extends RabbitMqExchangeListener{

   //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbStateChangedHandler.class);
    private static final String[] subscribedTo = new String[]{
        BulbSearchFinished.class.getName()
    };
    @Autowired 
    private Gson gson;
    @Autowired
    IBulbBridgeAdminServiceInternal bridgeAdminServiceInternal;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbSearchFinishedHandler() {
        super();
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected void filteredDispatch(String type, String message) {
        Notification nf = gson.fromJson(message, Notification.class);
        if(log.isDebugEnabled()){
            log.debug(" -- [ X ] Going to sync internal bridge state.");
        }
        BulbSearchFinished evt = gson.fromJson(nf.getEventBody(), BulbSearchFinished.class);
        bridgeAdminServiceInternal.syncToHardwareStateInternal(
                new BulbBridgeId(evt.getBulbBridgeId()), 
                evt.getPrincipal()
        );
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
