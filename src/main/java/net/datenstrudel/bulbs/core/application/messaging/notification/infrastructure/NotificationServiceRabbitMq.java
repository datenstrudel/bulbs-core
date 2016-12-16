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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
        Stream<Notification> notifications = listUnpublishedNotifications(
                tracker.getMostRecentPublishedStoredEventId());

        Notification currentNf = null;
        Notification previousNf = null;
        Iterator<Notification> it = notifications.iterator();

        while( it.hasNext() ){
            currentNf = it.next();
            try {
                if(log.isDebugEnabled()) log.debug("|-- [ -> ] Publishing " + currentNf);
                publish(currentNf);
                previousNf = currentNf;
            } catch (IOException ex) {
                log.error(" -- [ -> ] Unable to send " + currentNf + " on topic["+TOPIC+"]", ex);
                if(previousNf != null) trackMostRecentPublishedMessage(tracker, previousNf);
                throw new IllegalStateException(
                        "Unable to send notification for topic["+TOPIC+"]", ex);
            }
        }
        if(currentNf != null) trackMostRecentPublishedMessage(tracker, currentNf);

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
//        channel.close();
    }
    private Stream<Notification> listUnpublishedNotifications(Long mostRecentStoredEventId){
        List<StoredEvent> events = eventStore.loadAllStoredEventsSince(mostRecentStoredEventId);
        return events.stream().map(this::notificationFrom);
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
        messageTrackerStore.save(tracker);
    }
    private PublishedMessageTracker publishedMessageTracker(){
        PublishedMessageTracker res = messageTrackerStore.findByType(TOPIC);
        if(res == null) res = new PublishedMessageTracker(TOPIC);
        return res;
    }

}
