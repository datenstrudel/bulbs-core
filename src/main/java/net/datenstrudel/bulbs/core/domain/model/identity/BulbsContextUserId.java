package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public class BulbsContextUserId 
        implements ValueObject<BulbsContextUserId>, Serializable {

    private String uuid;
    
	private BulbsContextUserId(){

	}
	public  BulbsContextUserId(String uuid){
        this.uuid = uuid;
	}

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean sameValueAs(BulbsContextUserId other) {
        if(other == null) return false;
        if(!this.uuid.equals(other.uuid))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.uuid);
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
        final BulbsContextUserId other = (BulbsContextUserId) obj;
        return this.sameValueAs(other);
    }
    @Override
    public String toString() {
        return "BulbsContextUserId{" + uuid + '}';
    }

}