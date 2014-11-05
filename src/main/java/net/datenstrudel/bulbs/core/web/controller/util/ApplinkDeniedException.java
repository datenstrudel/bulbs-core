package net.datenstrudel.bulbs.core.web.controller.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Thomas Wendzinski
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
@Deprecated
public class ApplinkDeniedException extends RuntimeException {

    public ApplinkDeniedException() {
    }

    
    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    public ApplinkDeniedException(String message) {
        super(message);
    }

}
