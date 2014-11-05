package net.datenstrudel.bulbs.core.web.controller.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Thomas Wendzinski
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAuthorizedException extends RuntimeException{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public NotAuthorizedException() {
    }
    public NotAuthorizedException(String message) {
        super(message);
    }
    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
