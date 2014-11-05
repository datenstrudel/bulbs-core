package net.datenstrudel.bulbs.core.application.messaging;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventSubscriber;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Aspect
@Component(value="bulbsCoreEventProcessor")
public class BulbsCoreEventProcessor {

    //~ Member(s) //////////////////////////////////////////////////////////////
    public static final String EVENT_PROCESSOR_ID = "domainEventProcessor_eventStore";
    
    private static DomainEventStore eventStore;
    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public void setEventStore(DomainEventStore eventStore) {
        this.eventStore = eventStore;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Before( "execution(* net.datenstrudel.bulbs.core.application.services.*.*(..))" )
    public void listenToDomainEvents() throws Throwable{
        DomainEventPublisher.instance().subscribe(EVENT_PROCESSOR_ID, new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(DomainEvent event) {
                eventStore.store(event);
            }
            @Override
            public Class<DomainEvent> subscribedToEventType() {
                return DomainEvent.class;
            }
        });
    }

    @Deprecated
    @Before( "execution(* net.datenstrudel.bulbs.core.infrastructure.Runnable_EventPublishingAware.execute(..))" )
    public void listenToDomainEvents_withinAsyncContext() throws Throwable{
        DomainEventPublisher.instance().reset();
        DomainEventPublisher.instance().subscribe(EVENT_PROCESSOR_ID, new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(DomainEvent event) {
                eventStore.store(event);
            }
            @Override
            public Class<DomainEvent> subscribedToEventType() {
                return DomainEvent.class;
            }
        });
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
