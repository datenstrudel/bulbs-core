package net.datenstrudel.bulbs.core.infrastructure;

import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventSubscriber;

/**
 * Abstract {@link java.lang.Runnable} implementation that enables {@link net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher}
 * to listen to events that might be triggered within asynchronous execution.
 * 
 * @author Thomas Wendzinski
 */
public abstract class Runnable_EventPublishingAware implements Runnable{

    public static final String EVENT_PROCESSOR_ID = "runnableEvtPubAware_eventStore";

    @Override
    public void run() {
        listenToDomainEvents();
        execute();
    }
    public abstract void execute();

    private final void listenToDomainEvents(){
        DomainEventPublisher.instance().reset();
        DomainEventPublisher.instance().subscribe(EVENT_PROCESSOR_ID,
                new DomainEventSubscriber<DomainEvent>() {
            @Override
            public void handleEvent(DomainEvent event) {
                final DomainEventStore eventStore = DomainServiceLocator.getBean(DomainEventStore.class);
                eventStore.store(event);
            }
            @Override
            public Class<DomainEvent> subscribedToEventType() {
                return DomainEvent.class;
            }
        });
    }
}
