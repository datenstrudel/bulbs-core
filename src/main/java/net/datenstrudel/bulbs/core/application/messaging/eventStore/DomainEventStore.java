package net.datenstrudel.bulbs.core.application.messaging.eventStore;

import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
public interface DomainEventStore {

    //~ Method(s) //////////////////////////////////////////////////////////////
    public Long nextIdentity();
    public StoredEvent loadById(Long id);
    public List<StoredEvent> loadAllStoredEventsSince(
            long mostRecentPublishedStoredEventId);
    public Long store(DomainEvent event);
    
    public DomainEvent fromStoredEvent(StoredEvent in);
    

}
