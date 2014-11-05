package net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import java.util.Date;
import java.util.Objects;

/**
 * Wraps a serialized domain event to be processed through a messaging middleware or similar
 * message distribution mechanism.
 * 
 * @author Thomas Wendzinski
 */
public class Notification implements ValueObject<Notification>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private Long notificationId;
    private String type;
    private Date occuredOn;
    
    private String eventBody;

    //~ Construction ///////////////////////////////////////////////////////////
    private Notification() {
    }
    public Notification(Long notificationId, String type, Date occuredOn, String eventBody) {
        this.notificationId = notificationId;
        this.type = type;
        this.occuredOn = occuredOn;
        this.eventBody = eventBody;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public Long getNotificationId() {
        return notificationId;
    }
    public String getType() {
        return type;
    }
    public Date getOccuredOn() {
        return occuredOn;
    }
    public String getEventBody() {
        return eventBody;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.notificationId);
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.occuredOn);
        hash = 29 * hash + Objects.hashCode(this.eventBody);
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
        final Notification other = (Notification) obj;
        return this.sameValueAs(other);
    }
    @Override
    public boolean sameValueAs(Notification other) {
        if(other == null) return false;
        if (!Objects.equals(this.notificationId, other.notificationId)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.occuredOn, other.occuredOn)) {
            return false;
        }
        if (!Objects.equals(this.eventBody, other.eventBody)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "Notification{" + "notificationId=" + notificationId + ", type=" + type + '}';
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    private void setType(String type) {
        this.type = type;
    }
    private void setOccuredOn(Date occuredOn) {
        this.occuredOn = occuredOn;
    }
    private void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }
    

}
