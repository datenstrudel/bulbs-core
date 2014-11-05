package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import java.io.Serializable;

/**
 *
 * @author Thomas Wendzinski
 */
public enum BulbsPrincipalState implements ValueObject<BulbsPrincipalState>, Serializable{

    REQUESTED,
    PENDING,
    REGISTERED,
    ;

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public boolean sameValueAs(BulbsPrincipalState other) {
        if(other == null)return false;
        return this == other;
    }

}
