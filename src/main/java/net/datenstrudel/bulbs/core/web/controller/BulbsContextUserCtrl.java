package net.datenstrudel.bulbs.core.web.controller;

import com.wordnik.swagger.annotations.Api;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.BulbsContextUserService;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser;
import net.datenstrudel.bulbs.core.web.controller.util.AuthenticationException;
import net.datenstrudel.bulbs.shared.domain.model.client.common.DtoSingleValue;
import net.datenstrudel.bulbs.shared.domain.model.client.identity.DtoBulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.validation.ValidationException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @author Thomas Wendzinski
 */
@RestController
@RequestMapping(value = "/core/")
@Api(value="identity", description = "manage user(s).", position = 1)
public class BulbsContextUserCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserCtrl.class);
    @Autowired
    private BulbsContextUserService userService;
    @Autowired 
    private ModelFacadeOutPort modelPort;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping ( method=RequestMethod.GET, value = "identity")
    public DtoBulbsContextUser current(
            Authentication authentication
    ){
        if(authentication == null || authentication.getPrincipal() == null)
             throw new AuthenticationException("User not authenticated");
        BulbsContextUserId userId = ((BulbsContextUser)authentication.getPrincipal()).getBulbsContextUserlId();
        modelPort.registerConverterFor(DtoBulbsContextUser.class);
        userService.loadById(userId);
        return modelPort.outputAs(DtoBulbsContextUser.class);
    }
            
    @RequestMapping ( method=RequestMethod.POST, value = "identity/signIn")
    public DtoBulbsContextUser signIn(
            @RequestBody @Valid DtoBulbsContextUser input
    ){

        modelPort.registerConverterFor(DtoBulbsContextUser.class);
        userService.signIn(input.getEmail(), input.getPassword());
        return modelPort.outputAs(DtoBulbsContextUser.class);
    }
    
    @RequestMapping ( method=RequestMethod.POST, value = "identity/signUp")
    public DtoBulbsContextUser signUp(
            @RequestBody @Valid DtoBulbsContextUser input
    ){
        
        modelPort.registerConverterFor(DtoBulbsContextUser.class);
        userService.signUp(
                input.getEmail(),
                input.getPassword(),
                input.getNickname(),
                new ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser() {
                    @Override
                    public void handleDuplicateEmail(String mailAddressConcerned) {
                        throw new IllegalArgumentException("Mail address already in use:" + mailAddressConcerned);
                    }
                    @Override
                    public void handleInvalidPassword() {
                        throw new ValidationException("Password size was not correct.");
                    }
                });
        return modelPort.outputAs(DtoBulbsContextUser.class);
    }

    @RequestMapping ( method=RequestMethod.PUT, value = "identity/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyPassword(
            @RequestBody @Valid DtoSingleValue input,
            Authentication authentication
    ){
        if(authentication == null || authentication.getPrincipal() == null)
            throw new AuthenticationException("User not authenticated");
        BulbsContextUser principal = (BulbsContextUser) authentication.getPrincipal();

        userService.modifyPassword(principal.getApiKey(), input.getValue(),
                new ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser() {
                    @Override
                    public void handleDuplicateEmail(String mailAddressConcerned) {
                        throw new IllegalArgumentException("Mail address already in use:" + mailAddressConcerned);
                    }
                    @Override
                    public void handleInvalidPassword() {
                        throw new ValidationException("Password size was not correct.");
                    }
                }
        );

    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
