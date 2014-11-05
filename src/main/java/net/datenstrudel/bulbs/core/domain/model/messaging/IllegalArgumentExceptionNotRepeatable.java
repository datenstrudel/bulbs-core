package net.datenstrudel.bulbs.core.domain.model.messaging;

/**
 *
 * @author Thomas Wendzinski
 */
public class IllegalArgumentExceptionNotRepeatable extends ProcessNotRepeatableException{


    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public IllegalArgumentExceptionNotRepeatable() {
    }
    public IllegalArgumentExceptionNotRepeatable(String message) {
        super(message);
    }
    public IllegalArgumentExceptionNotRepeatable(String message, Throwable cause) {
        super(message, cause);
    }
    public IllegalArgumentExceptionNotRepeatable(Throwable cause) {
        super(cause);
    }
    public IllegalArgumentExceptionNotRepeatable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
