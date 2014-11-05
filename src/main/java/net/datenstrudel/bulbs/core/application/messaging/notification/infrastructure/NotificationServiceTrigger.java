package net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure;

import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Triggers registered notification services in order to forward events to a message broker.
 * @author Thomas Wendzinski
 */
@Component
public class NotificationServiceTrigger {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceTrigger.class);
    @Autowired
    @Qualifier(value = "coreInternalNotificationService")
    private NotificationService nfService_coreInternal;

    //~ Construction ///////////////////////////////////////////////////////////

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Scheduled(fixedRate = 500)
    public void trigger_coreInternal(){
        nfService_coreInternal.publishNotifications();
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    /** For Tests only */
    private void setNfService_coreInternal(NotificationService nfService_coreInternal) {
        this.nfService_coreInternal = nfService_coreInternal;
    }
    
}
