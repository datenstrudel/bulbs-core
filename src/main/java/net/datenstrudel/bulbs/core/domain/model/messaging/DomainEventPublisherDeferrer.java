package net.datenstrudel.bulbs.core.domain.model.messaging;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class enables publishing of events in a deferred fashion, by discarding all events
 * except the last of events that are grouped by {@link DeferrableDomainEvent#eventTypeDiscriminator() }
 * and occur within a duty cycle of 1500ms. Its intended to be used by {@link DomainEventPublisher},
 * due to that class usually already knows all subscribers to domain events.
 * 
 * @author Thomas Wendzinski
 */
@Component
public class DomainEventPublisherDeferrer {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisherDeferrer.class);

    private static final Map<String, DeferrableDomainEvent> deferredEvents = new ConcurrentHashMap<>();
    private static final Map<String, Collection<DomainEventSubscriber>> deferredEventsSubscribers = new ConcurrentHashMap<>();
    
    //~ Construction ///////////////////////////////////////////////////////////
    public DomainEventPublisherDeferrer() {
        log.info("DomainEventPublisherDeferrer INITIALIZED." );
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    protected <T extends DeferrableDomainEvent> void publishDeferred(
            final T deferrableEvent, 
            Collection<DomainEventSubscriber> registerredSubscribers){
        deferredEvents.put(deferrableEvent.eventTypeDiscriminator(), deferrableEvent);
        deferredEventsSubscribers.put(
                deferrableEvent.eventTypeDiscriminator(), 
                registerredSubscribers);
    }
    
    @Scheduled(fixedRate = 1500)
    public void publishDeferred(){
        log.debug("Publishing deferred events.. " + deferredEvents.size());
        DomainEventPublisher publisher = DomainEventPublisher.instance();
        for (Entry<String, DeferrableDomainEvent> el : deferredEvents.entrySet()) {
            publisher.reset();
            int discr = 0;
            for (DomainEventSubscriber subscriber : deferredEventsSubscribers.get(el.getKey())) {
                publisher.subscribe("" + discr, subscriber);
                discr ++;
            }
            synchronized(deferredEvents){
                deferredEvents.remove(el.getKey());
                synchronized(deferredEventsSubscribers){
                    deferredEventsSubscribers.remove(el.getKey());
                }
            }
            publisher.publish(el.getValue());
            publisher.reset();
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
