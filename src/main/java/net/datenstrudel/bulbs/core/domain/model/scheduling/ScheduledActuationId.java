package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import javax.validation.ValidationException;
import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 */
public class ScheduledActuationId implements ValueObject<ScheduledActuationId>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String uuid;
    private BulbsContextUserId userId;
    
    //~ Construction ///////////////////////////////////////////////////////////
    private ScheduledActuationId() {
    }
    public ScheduledActuationId(String uuid, BulbsContextUserId userId) {
        this.uuid = uuid;
        this.userId = userId;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getUuid() {
        return uuid;
    }
    public BulbsContextUserId getUserId() {
        return userId;
    }

    public String serialize(){
        return uuid + userId.getUuid();
    }
    public static ScheduledActuationId fromSerialized(String in){
        if(in.length() < 37) throw new ValidationException("Id was too short");
        String uuid = in.substring(0, 36);
        String userId = in.substring(36);
        return new ScheduledActuationId(uuid, new BulbsContextUserId(userId));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.uuid);
        hash = 89 * hash + Objects.hashCode(this.userId);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScheduledActuationId other = (ScheduledActuationId) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }
    @Override
    public boolean sameValueAs(ScheduledActuationId other) {
        return this.equals(other);
    }
    @Override
    public String toString() {
        return "ScheduledActuationId{" + "uuid=" + uuid + '}';
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setUserId(BulbsContextUserId userId) {
        this.userId = userId;
    }
    private void setUuid(String uuid) {
        this.uuid = uuid;
    }
    

}
