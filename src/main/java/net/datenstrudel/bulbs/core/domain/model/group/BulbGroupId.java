package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbGroupId implements ValueObject<BulbGroupId>, Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private BulbsContextUserId userId;
    private String groupUuid;

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbGroupId() {
    }
    public BulbGroupId(BulbsContextUserId userId, String groupUuid) {
        this.userId = userId;
        this.groupUuid = groupUuid;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbsContextUserId getUserId() {
        return userId;
    }
    public String getGroupUuid() {
        return groupUuid;
    }

    public void setUserId(BulbsContextUserId userId) {
        this.userId = userId;
    }
    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String serialize(){
        return groupUuid + userId.getUuid();
    }
    public static BulbGroupId fromSerialized(String in){
        if(in.length() < 37)throw new ValidationException("Id was too short");
        String groupUuId = in.substring(0, 36);
        String userId = in.substring(36);
        return new BulbGroupId(new BulbsContextUserId(userId), groupUuId);
    }
    
    @Override
    public boolean sameValueAs(BulbGroupId other) {
        if(other == null) return false;
        if(!this.userId.sameValueAs(other.userId))return false;
        if(!this.groupUuid.equals(other.groupUuid))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.userId);
        hash = 17 * hash + Objects.hashCode(this.groupUuid);
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
        final BulbGroupId other = (BulbGroupId) obj;
        return this.sameValueAs(other);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
