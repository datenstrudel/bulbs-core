package net.datenstrudel.bulbs.core.application.messaging.notification;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;

/**
 * An implementation is capable of retrieving {@link StoredEvent} instance by the help
 * of the topic specific {@link PublishedMessageTracker} which it receives from the event
 * store as well.<br />
 * Eventually this service publishes these events as {@link net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure.Notification}s using its
 * implementation specific messaging/communication mechanism.
 * 
 * @author Thomas Wendzinski
 */
public interface NotificationService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public void publishNotifications();
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
