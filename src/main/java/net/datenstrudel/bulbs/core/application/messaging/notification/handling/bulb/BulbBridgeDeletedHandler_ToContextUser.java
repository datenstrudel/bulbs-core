package net.datenstrudel.bulbs.core.application.messaging.notification.handling.bulb;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.application.services.IBulbBridgeAdminServiceInternal;
import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbBridgeDeleted;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class BulbBridgeDeletedHandler_ToContextUser extends RabbitMqExchangeListener {
    
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeDeletedHandler_ToContextUser.class);
    private static final String[] subscribedTo = new String[]{
            BulbBridgeDeleted.class.getName()
    };
    @Autowired 
    private Gson gson;
    @Autowired
    IBulbBridgeAdminServiceInternal bulbBridgeAdminService;
    
    @Override
    protected String topicName() {
        return Exchanges.EXCHANGE_TOPIC__BUBLS_CORE;
    }

    @Override
    protected String[] listensToEvents() {
        return subscribedTo;
    }

    @Override
    protected void filteredDispatch(String type, String message) {
        Notification nf = gson.fromJson(message, Notification.class);
        if(log.isDebugEnabled()){
            log.debug("|-- [ X ] Going to handle retrieved message containing payload type["+type+"]");
            log.debug(" -- [ X ] Going to handle retrieved Notification with id["+nf.getNotificationId()+"]");
        }
        BulbBridgeDeleted evt = gson.fromJson(nf.getEventBody(), BulbBridgeDeleted.class);
        bulbBridgeAdminService.removeAllBulbsPrincipalsAfterBridgeDeletion(
                evt.getUserId(),
                evt.getBridgeId(),
                evt.getBridgeAddress(),
                evt.getPlatform()
        );
    }

}
