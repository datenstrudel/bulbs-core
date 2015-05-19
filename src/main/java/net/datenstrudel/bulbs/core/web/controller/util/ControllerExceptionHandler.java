package net.datenstrudel.bulbs.core.web.controller.util;

import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ValidationException;

/**
 * Global Exception handling
 * @author Thomas Wendzinski
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @ExceptionHandler( {
        UsernameNotFoundException.class,
        AuthenticationException.class 
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleSecurityException(
            Exception ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){
        
        log.debug("Handling security exception.. ");
        return ex.getMessage();
        
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handle(
            BadCredentialsException ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){
        
        log.debug("Handling BadCredentialsException.. ");
        return ex.getMessage();
        
    }
    
    @ExceptionHandler({
        IllegalArgumentException.class,
        MethodArgumentNotValidException.class,
        BadRequestException.class,
        MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleInvalidRequest(
            Exception ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){

        log.info("Handling Invalid Request Exception of type[" + ex.getClass().getSimpleName() + "]: " + ex.getMessage());
        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException manvex = (MethodArgumentNotValidException) ex;
            StringBuilder rejFields = new StringBuilder("Fields rejected or missing: ");
            for (FieldError el : manvex.getBindingResult().getFieldErrors()) {
                rejFields.append(el.getField()).append(", ");
            }
            return rejFields.substring(0, rejFields.length()-1);

        }
        return ex.getMessage();
    }

    @ExceptionHandler(BulbBridgeHwException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public String handle(
            BulbBridgeHwException ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){
        
        log.info("Handling BulbBridgeHwException: " + ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ResponseBody
    public String handle(
            UnsupportedOperationException ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){
        
        log.warn("Handling UnsupportedOperationException: " + ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handle(
            ValidationException ex,
            HttpServletRequest req,
            HttpServletResponse resp,
            HttpSession session
    ) {
        return ex.getMessage();
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handle(
            Exception ex, 
            HttpServletRequest req, 
            HttpServletResponse resp, 
            HttpSession session){
        
        log.error("Handling general Exception!", ex);
        return ex.getMessage();
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

    
}
