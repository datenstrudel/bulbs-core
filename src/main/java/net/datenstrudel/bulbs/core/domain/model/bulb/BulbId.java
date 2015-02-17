package net.datenstrudel.bulbs.core.domain.model.bulb;
import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import javax.validation.ValidationException;
import java.io.Serializable;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public class BulbId implements ValueObject<BulbId>, Serializable{

	private String localId;
    private BulbBridgeId bridgeId;

	private BulbId(){}
    /**
     * 
     * @param bridgeId parent bridge id
     * @param localId local id within <code>bridgeId</code>'s corresponding entity
     */
    public BulbId(BulbBridgeId bridgeId, String localId) {
        this.bridgeId = bridgeId;
        this.localId = localId;
    }

    public String getLocalId() {
        return localId;
    }
    public BulbBridgeId getBridgeId() {
        return bridgeId;
    }

    public String serialize(){
        return bridgeId.getUuId() + localId;
    }
    public static BulbId fromSerialized(String in){
        if(in.length() < 37)throw new ValidationException("Id was too short");
        String bridgeUuId = in.substring(0, 36);
        String localId = in.substring(36);
        return new BulbId(new BulbBridgeId(bridgeUuId), localId);
    }

    @Override
    public boolean sameValueAs(BulbId other) {
        if(other == null)return false;
        if(!this.bridgeId.sameValueAs(other.bridgeId))return false;
        if(!this.localId.equals(other.localId))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.localId != null ? this.localId.hashCode() : 0);
        hash = 97 * hash + (this.bridgeId != null ? this.bridgeId.hashCode() : 0);
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
        final BulbId other = (BulbId) obj;
        return this.sameValueAs(other);
    }

    @Override
    public String toString() {
        return "BulbId{" + "localId=" + localId + ", bridgeId=" + bridgeId + '}';
    }
    
}