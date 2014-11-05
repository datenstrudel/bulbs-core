package net.datenstrudel.bulbs.core.application.messaging.eventStore;

import net.datenstrudel.bulbs.shared.domain.model.Entity;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 */
public class StoredEvent extends Entity<StoredEvent, Long> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String eventBody;
    private Date occuredOn;
    private String typeName;

    //~ Construction ///////////////////////////////////////////////////////////
    private StoredEvent(){}
    /**
     * Provided for persistence layer.
     * @param id
     * @param eventBody
     * @param occuredOn
     * @param typeName 
     */
    public StoredEvent(Long id, String eventBody, Date occuredOn, String typeName) {
        setId(id);
        this.eventBody = eventBody;
        this.occuredOn = occuredOn;
        this.typeName = typeName;
    }
    public StoredEvent(
            String eventBody, 
            Date occuredOn, 
            String typeName) {
        this.eventBody = eventBody;
        this.occuredOn = occuredOn;
        this.typeName = typeName;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getEventBody() {
        return eventBody;
    }
    public Date getOccuredOn() {
        return occuredOn;
    }
    public String getTypeName() {
        return typeName;
    }

    private void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }
    private void setOccuredOn(Date occuredOn) {
        this.occuredOn = occuredOn;
    }
    private void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public boolean sameIdentityAs(StoredEvent other) {
        if(other == null) return false;
        return this.getId().equals(other.getId());
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.getId());
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
        final StoredEvent other = (StoredEvent) obj;
        return this.sameIdentityAs(other);
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////


}
