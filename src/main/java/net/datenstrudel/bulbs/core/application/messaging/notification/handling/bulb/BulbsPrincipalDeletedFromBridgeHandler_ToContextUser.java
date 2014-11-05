package net.datenstrudel.bulbs.core.application.messaging.notification.handling.bulb;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.application.services.BulbsContextUserService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsPrincipalDeletedFromBridge;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Thomas Wendzinski.
 */
@Component
public class BulbsPrincipalDeletedFromBridgeHandler_ToContextUser extends RabbitMqExchangeListener{

    private static final Logger log = LoggerFactory.getLogger(BulbBridgeDeletedHandler_ToContextUser.class);
    private static final String[] subscribedTo = new String[]{
            BulbsPrincipalDeletedFromBridge.class.getName()
    };
    @Autowired
    private Gson gson;
    @Autowired
    BulbsContextUserService userService;

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
        BulbsPrincipalDeletedFromBridge evt = gson.fromJson(nf.getEventBody(), BulbsPrincipalDeletedFromBridge.class);
        userService.removeBulbsPrincipal(
                new BulbsContextUserId(evt.getContextUserId()),
                evt.getPrincipalDeleted() );
    }
}
