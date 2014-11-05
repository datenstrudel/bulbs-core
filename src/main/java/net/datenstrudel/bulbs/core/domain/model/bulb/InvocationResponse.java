package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 */
public class InvocationResponse implements ValueObject<InvocationResponse> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String message;
    private boolean error;

    //~ Construction ///////////////////////////////////////////////////////////
    public InvocationResponse(String message, boolean error) {
        this.message = message;
        this.error = error;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getMessage() {
        return message;
    }
    public boolean isError() {
        return error;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public boolean sameValueAs(InvocationResponse other) {
        return this.equals(other);
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.message);
        hash = 97 * hash + (this.error ? 1 : 0);
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
        final InvocationResponse other = (InvocationResponse) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (this.error != other.error) {
            return false;
        }
        return true;
    }

    private void setMessage(String message) {
        this.message = message;
    }
    private void setError(boolean error) {
        this.error = error;
    }
    

}
