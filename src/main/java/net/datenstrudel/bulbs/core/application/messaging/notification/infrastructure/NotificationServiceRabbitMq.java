package net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;
import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Wendzinski
 */
public class NotificationServiceRabbitMq implements NotificationService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceRabbitMq.class);
    private final String TOPIC;
    
    @Autowired
    private PublishedMessageTrackerStore messageTrackerStore;
    @Autowired
    private DomainEventStore eventStore;
    @Autowired
    @Qualifier(value = "rabbitConnectionFactory")
    private ConnectionFactory rabbitConnectionFactory;
    @Autowired
    private Gson objectSerializer;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public NotificationServiceRabbitMq(String topic) {
        this.TOPIC = topic;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void publishNotifications(){
        PublishedMessageTracker tracker = publishedMessageTracker();
        List<Notification> notifications = listUnpublishedNotifications(
                tracker.getMostRecentPublishedStoredEventId());
        if(notifications.isEmpty())return ;
        
        int i = 0;
        for (Notification notification : notifications) {
            try {
                if(log.isDebugEnabled()) log.debug("|-- [ -> ] Publishing " + notification);
                publish(notification);
                i++;
            } catch (IOException ex) {
                log.error(" -- [ -> ] Unable to send "+notification+" on topic["+TOPIC+"]", ex);
                trackMostRecentPublishedMessage(tracker, notifications.get(i));
                throw new IllegalStateException(
                        "Unable to send notification for topic["+TOPIC+"]", ex);
            }
        }
        trackMostRecentPublishedMessage(tracker, notifications.get(i-1));

    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void publish(Notification notification) throws IOException{
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType(notification.getType())
                .messageId(String.valueOf(notification.getNotificationId()))
                .timestamp(notification.getOccuredOn())
                .build();
        
        Channel channel = rabbitConnectionFactory.createConnection().createChannel(false);
        channel.exchangeDeclare(TOPIC, "topic", true);
        channel.basicPublish(
                TOPIC, 
                notification.getType(),
                props, 
                objectSerializer.toJson(notification).getBytes());
        channel.close();

    }
    private List<Notification> listUnpublishedNotifications(Long mostRecentStoredEventId){
        List<StoredEvent> events = eventStore.loadAllStoredEventsSince(mostRecentStoredEventId);
        List<Notification> res = new ArrayList<>(events.size());
        for (StoredEvent el : events) {
            res.add(notificationFrom(el));
        }
        return res;
    }
    private Notification notificationFrom(StoredEvent event){
        return new Notification(
                event.getId(), 
                event.getTypeName(), 
                event.getOccuredOn(), 
                event.getEventBody());
    }
    
    private void trackMostRecentPublishedMessage(
            PublishedMessageTracker tracker, Notification lastSuccessfulNotification){
        tracker.setMostRecentPublishedStoredEventId(lastSuccessfulNotification.getNotificationId());
        messageTrackerStore.store(tracker);
    }
    private PublishedMessageTracker publishedMessageTracker(){
        PublishedMessageTracker res = messageTrackerStore.loadByType(TOPIC);
        if(res == null) res = new PublishedMessageTracker(TOPIC);
        return res;
    }

}
