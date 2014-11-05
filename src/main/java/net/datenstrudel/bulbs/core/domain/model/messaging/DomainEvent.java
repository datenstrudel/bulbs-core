package net.datenstrudel.bulbs.core.domain.model.messaging;

import java.util.Date;

/**
 * Describes an action that has taken place in the past, containing information necessary
 * for independent modules, that might need to react to it in an independent way. 
 * The information of an event can be even relevant for distributed systems, running outside
 * the runtime within an event was actually emitted.
 * 
 * @see DomainEventPublisher
 * @see DomainEventSubscriber
 * @author Thomas Wendzinski
 */
public interface DomainEvent {
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * @return the event's unique id.
     */
    public String getDomainId();
    /**
     * @return time of occurence 
     */
    public Date getOccurredOn();

}
