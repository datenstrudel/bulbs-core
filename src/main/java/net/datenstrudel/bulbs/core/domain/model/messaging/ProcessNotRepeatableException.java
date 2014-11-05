package net.datenstrudel.bulbs.core.domain.model.messaging;

/**
 * Base class for exceptions that shall mark an event handling process as failed 
 * and thus causes the {@link DomainEvent}, that initially triggered the process, beeing
 * finally rejected (deleted).
 * 
 * @author Thomas Wendzinski
 */
public class ProcessNotRepeatableException extends RuntimeException{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public ProcessNotRepeatableException() {
    }
    public ProcessNotRepeatableException(String message) {
        super(message);
    }
    public ProcessNotRepeatableException(String message, Throwable cause) {
        super(message, cause);
    }
    public ProcessNotRepeatableException(Throwable cause) {
        super(cause);
    }
    public ProcessNotRepeatableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
