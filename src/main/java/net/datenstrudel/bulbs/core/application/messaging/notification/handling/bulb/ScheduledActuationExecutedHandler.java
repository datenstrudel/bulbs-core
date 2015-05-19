package net.datenstrudel.bulbs.core.application.messaging.notification.handling.bulb;

import com.google.gson.Gson;
import net.datenstrudel.bulbs.core.application.messaging.notification.Exchanges;
import net.datenstrudel.bulbs.core.application.messaging.notification.handling.RabbitMqExchangeListener;
import net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification;
import net.datenstrudel.bulbs.core.application.services.ScheduledActuationServiceInternal;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationExecuted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ScheduledActuationExecutedHandler extends RabbitMqExchangeListener{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationExecutedHandler.class);
    private static final String[] subscribedTo = new String[]{
        ScheduledActuationExecuted.class.getName()
    };
    @Autowired
    private Gson gson;
    @Autowired
    ScheduledActuationServiceInternal scheduledActuationService;

    //~ Construction ///////////////////////////////////////////////////////////
    public ScheduledActuationExecutedHandler() {
        super();
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected void filteredDispatch(String type, String message) {
        Notification nf = gson.fromJson(message, Notification.class);

        ScheduledActuationExecuted evt = gson.fromJson(nf.getEventBody(), ScheduledActuationExecuted.class);
        scheduledActuationService.deleteAfterExecutionIfConfigured(evt.getEntityId());
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
