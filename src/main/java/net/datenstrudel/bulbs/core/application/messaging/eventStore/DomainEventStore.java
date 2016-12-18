package net.datenstrudel.bulbs.core.application.messaging.eventStore;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
public interface DomainEventStore {

    //~ Method(s) //////////////////////////////////////////////////////////////
    Long nextIdentity();

    StoredEvent loadById(Long id);

    List<StoredEvent> loadAllStoredEventsSince(long mostRecentPublishedStoredEventId);

    Long store(DomainEvent event);

    DomainEvent fromStoredEvent(StoredEvent in);
    

}
