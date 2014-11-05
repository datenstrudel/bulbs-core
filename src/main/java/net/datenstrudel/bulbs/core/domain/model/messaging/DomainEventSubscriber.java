package net.datenstrudel.bulbs.core.domain.model.messaging;

/**
 * Event subscriber that is beeing implemented in order to trigger reaction(s) to 
 *  {@link DomainEvent}s.
 * 
 * @param <T> Type of {@link DomainEvent} <code>this</code> subscribes to.
 * @author Thomas Wendzinski
 */
public interface DomainEventSubscriber<T extends DomainEvent> {

    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * Implements subscriber specific actions to be taken as response to the
     * <code>event</code> occured.
     * @param event 
     */
    public void handleEvent(T event);
    /**
     * @return To which specific type of event <code>this</code> shall react on by having
     * {@link #handleEvent(net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent) handleEvent(..)} invoked
     */
    public Class<T> subscribedToEventType();
}
