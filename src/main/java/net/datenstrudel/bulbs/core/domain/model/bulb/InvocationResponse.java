package net.datenstrudel.bulbs.core.domain.model.bulb;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

/**
 *
 * @author Thomas Wendzinski
 */
public class InvocationResponse implements ValueObject<InvocationResponse> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private String message;
    private boolean error;
    private Map<String, Object> parsedVariables = new HashMap<>();

    //~ Construction ///////////////////////////////////////////////////////////
    public InvocationResponse(String message, boolean error) {
        this.message = message;
        this.error = error;
    }
    public InvocationResponse(String message, boolean error, Map<String, Object> parsedVars) {
        this.message = message;
        this.error = error;
        this.parsedVariables = parsedVars;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getMessage() {
        return message;
    }
    public boolean isError() {
        return error;
    }
    public Map<String, Object> getParsedVariables() {
        return parsedVariables;
    }

    public <T> Optional<T> getValueFromParsedResponse(String key, Class<T> type) {
        if (parsedVariables == null || !parsedVariables.containsKey(key)) {
            return Optional.empty();
        } else {
            return Optional.of((T) parsedVariables.get(key));
        }
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
        hash = 97 * hash + Objects.hashCode(this.parsedVariables);
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
        if (!Objects.equals(this.parsedVariables, other.parsedVariables)) {
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
    private void setParsedVariables(Map<String, Object> parsedVariables) {
        this.parsedVariables = parsedVariables;
    }
}
