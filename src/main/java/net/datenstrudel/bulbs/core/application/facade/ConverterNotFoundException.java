package net.datenstrudel.bulbs.core.application.facade;

/**
 *
 * @author Thomas Wendzinski
 */
public class ConverterNotFoundException extends RuntimeException{

    
    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public ConverterNotFoundException() {
    }
    public ConverterNotFoundException(String message) {
        super(message);
    }
    public ConverterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConverterNotFoundException(Throwable cause) {
        super(cause);
    }
    public ConverterNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
