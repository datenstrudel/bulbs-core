package net.datenstrudel.bulbs.core.application.messaging.eventStore;

/**
 *
 * @author Thomas Wendzinski
 */
public interface PublishedMessageTrackerStore {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public void store(PublishedMessageTracker msgTracker);
    public PublishedMessageTracker loadByType(String type);

}
