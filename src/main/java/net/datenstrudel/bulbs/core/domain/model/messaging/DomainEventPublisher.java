package net.datenstrudel.bulbs.core.domain.model.messaging;

import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * An event publisher that is working on a per-thread basis.
 * Shall be reset on every new request, that works on a thread that is reused (by pooling).
 * 
 * @author Thomas Wendzinski
 */
public class DomainEventPublisher {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisher.class);

    private static final ThreadLocal<Map> subscribers = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> publishing = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private final DomainEventPublisherDeferrer publisherDeferrer;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public DomainEventPublisher() {
        this.publisherDeferrer = DomainServiceLocator.getBean(DomainEventPublisherDeferrer.class);
    }
    public static DomainEventPublisher instance(){
        return new DomainEventPublisher();
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public <T extends DeferrableDomainEvent> void publishDeferred(final T domainEvent){
        if(publisherDeferrer == null){
            log.warn("No publishing event deferrer injected; Going to publish immediately.");
            publish(domainEvent);
            return;
        }
        if(subscribers.get() == null || subscribers.get().isEmpty()) return;
        publisherDeferrer.publishDeferred(domainEvent, subscribers.get().values());
    }
    public <T extends DomainEvent> void publish(final T domainEvent){
        if (publishing.get()){
            return ;
        }
        try{
            if(log.isDebugEnabled()){
                log.debug("[ -> ] Publishing Event["+domainEvent.getClass().getName()+"]");
            }
            publishing.set(Boolean.TRUE);
            Map<Integer, DomainEventSubscriber<T>> registeredSubscribers =
                    (Map) subscribers.get();
            if(registeredSubscribers != null){
                Class<?> eventType = domainEvent.getClass();
                Class<?> subscribedTo;
                for (DomainEventSubscriber<T> el : registeredSubscribers.values()) {
                    subscribedTo = el.subscribedToEventType();
                    if(subscribedTo == eventType ||
                            subscribedTo == DomainEvent.class){
                        el.handleEvent(domainEvent);
                    }
                }
            }
        }finally{
            publishing.set(Boolean.FALSE);
        }
    }
    public <T extends DomainEvent> void subscribe( String subscriberId, DomainEventSubscriber<T> subscriber){
        if (publishing.get())return ;
        
        Map<Integer, DomainEventSubscriber<T>> registeredSubscribers 
                = (Map)subscribers.get();
        if(registeredSubscribers == null){
            registeredSubscribers = new HashMap<>();
            subscribers.set(registeredSubscribers);
        }
        registeredSubscribers.put(subscriberId.hashCode(), subscriber);
    }
    
    public DomainEventPublisher reset(){
        if(!publishing.get())subscribers.set(null);
        return this;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////


}
