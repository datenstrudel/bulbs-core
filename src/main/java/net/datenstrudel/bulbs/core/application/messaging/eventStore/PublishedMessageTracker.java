package net.datenstrudel.bulbs.core.application.messaging.eventStore;

import net.datenstrudel.bulbs.shared.domain.model.Entity;

import java.util.Objects;

/**
 * Contains the ID of that {@link StoredEvent} which was most recently, successfully
 * handed over to a message channel of a specific type/topic.
 * 
 * @author Thomas Wendzinski
 */
public class PublishedMessageTracker extends Entity<PublishedMessageTracker, String>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    /**
     * Contains the type of topic/channel the events of this tracker were published on.
     */
    private String type;
    private long mostRecentPublishedStoredEventId;
    
    //~ Construction ///////////////////////////////////////////////////////////
    private PublishedMessageTracker() {
    }
    public PublishedMessageTracker(String type) {
        this.type = type;
        this.mostRecentPublishedStoredEventId = 0l;
    }
    public PublishedMessageTracker(String type, long mostRecentPublishedStoredEventId) {
        this.type = type;
        this.mostRecentPublishedStoredEventId = mostRecentPublishedStoredEventId;
    }    
    public PublishedMessageTracker(String id, String type, long mostRecentPublishedStoredEventId) {
        setId(id);
        this.type = type;
        this.mostRecentPublishedStoredEventId = mostRecentPublishedStoredEventId;
    }
    
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getType() {
        return type;
    }
    public long getMostRecentPublishedStoredEventId() {
        return mostRecentPublishedStoredEventId;
    }
    public  void setMostRecentPublishedStoredEventId(long mostRecentPublishedStoredEventId) {
        this.mostRecentPublishedStoredEventId = mostRecentPublishedStoredEventId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(getId());
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublishedMessageTracker other = (PublishedMessageTracker) obj;
        return this.sameIdentityAs(other);
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setType(String type) {
        this.type = type;
    }
    

}
