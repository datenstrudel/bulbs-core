package net.datenstrudel.bulbs.core.application.messaging.eventStore;

import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thomas Wendzinski
 */
@Repository
public interface PublishedMessageTrackerStore extends BCoreBaseRepository<PublishedMessageTracker, String> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public PublishedMessageTracker findByType(String type);

}
