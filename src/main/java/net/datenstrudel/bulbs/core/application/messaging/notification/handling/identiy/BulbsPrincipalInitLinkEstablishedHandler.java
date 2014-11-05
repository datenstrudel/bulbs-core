package net.datenstrudel.bulbs.core.application.messaging.notification.handling.identiy;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.application.services.BulbsContextUserService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsPrincipalInitLinkEstablished;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class BulbsPrincipalInitLinkEstablishedHandler 
        extends RabbitMqExchangeListener{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsPrincipalInitLinkEstablishedHandler.class);
    private static final String[] subscribedTo = new String[]{
        BulbsPrincipalInitLinkEstablished.class.getName()
    };
    @Autowired 
    private Gson gson;
    @Autowired
    private BulbsContextUserService userService;

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbsPrincipalInitLinkEstablishedHandler() {
        super();
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected void filteredDispatch(String type, String message) {
        log.info("|-- [ X ] Going to handle retrieved message containing payload type["+type+"]");
        Notification nf = gson.fromJson(message, Notification.class);
        log.info("|-- [ X ] Going to handle retrieved Notification with id["+nf.getNotificationId()+"]");
        BulbsPrincipalInitLinkEstablished evt = gson.fromJson(
                nf.getEventBody(), BulbsPrincipalInitLinkEstablished.class);
        
        userService.addBulbsPrincipal(
                new BulbsContextUserId(evt.getContextUserId()), evt.getPrincipal());
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
