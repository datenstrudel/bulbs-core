package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:41
 */
public class BulbBridgeId implements ValueObject<BulbBridgeId>, Serializable {

	private String uuId;

	private BulbBridgeId(){

	}
    public BulbBridgeId(String uuid){
        this.uuId = uuid;
    }

    public String getUuId() {
        return uuId;
    }
    
	/**
	 * 
	 * @param other
	 */
    @Override
	public boolean sameValueAs(BulbBridgeId other){
        if(other == null) return false;
        if(!this.uuId.equals(other.uuId))return false;
        return true;
		
	}
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.uuId);
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
        final BulbBridgeId other = (BulbBridgeId) obj;
        return this.sameValueAs(other);
    }

    @Override
    public String toString() {
        return uuId;
    }

    
    
}