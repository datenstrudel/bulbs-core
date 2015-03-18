package net.datenstrudel.bulbs.core.web.controller;

import com.wordnik.swagger.annotations.Api;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.ScheduledActuationService;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.shared.domain.model.client.scheduling.DtoScheduledActuation;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "core/schedules",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "schedules", description = "Schedule bulb-, preset- or group states ", position = 50)
public class ScheduledActuationCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired 
    private ModelFacadeOutPort modelPort;
    @Autowired
    private DtoConverterRegistry converterFactory;
    
    @Autowired 
    private ScheduledActuationService scheduledActuationService;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value="/{scheduledActuationId}", 
            produces = "application/json")
    public DtoScheduledActuation loadById(
            @PathVariable("scheduledActuationId") String _scheduledActuationId,
            Authentication authentication){
            
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        ScheduledActuationId schedId = ScheduledActuationId.fromSerialized(_scheduledActuationId);
        
        modelPort.registerConverterFor(DtoScheduledActuation.class);
        scheduledActuationService.loadById(principal.getBulbsContextUserlId(), schedId);
        return modelPort.outputAs(DtoScheduledActuation.class);
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value="/{scheduledActuationId}", 
            produces = "application/json")
    public void deleteById(
            @PathVariable("scheduledActuationId") String _scheduledActuationId,
            Authentication authentication ){
            
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        ScheduledActuationId schedId = ScheduledActuationId.fromSerialized(_scheduledActuationId);
        
        scheduledActuationService.remove(principal.getBulbsContextUserlId(), schedId);
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            produces = "application/json")
    public Set<DtoScheduledActuation> scheduledActuationsByUser(
            Authentication authentication){
            
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        
        modelPort.registerConverterFor(DtoScheduledActuation.class);
        scheduledActuationService.loadAllByUser(principal.getBulbsContextUserlId());
        return modelPort.outputAsSet(DtoScheduledActuation.class);
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            consumes = "application/json")
    public DtoScheduledActuation createAndActivate(
            final @RequestBody @Valid DtoScheduledActuation dtoNewSchedule,
            Authentication authentication
    ){
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        
        modelPort.registerConverterFor(DtoScheduledActuation.class);
        
        scheduledActuationService.createAndActivate(
                principal.getBulbsContextUserlId(), 
                dtoNewSchedule.getName(), 
                dtoNewSchedule.getTrigger(), 
                dtoNewSchedule.isDeleteAfterExecution(), 
                converterFactory
                        .converterForDomain(AbstractActuatorCmd.class)
                        .reverseConvertCollection((Collection) dtoNewSchedule.getStates()) );
        DtoScheduledActuation res = modelPort.outputAs(DtoScheduledActuation.class);
        return res;
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value="/{scheduledActuationId}/active/{active}")
    public void toggleActivation(
            @PathVariable("scheduledActuationId") String _scheduledActuationId,
            @PathVariable boolean active,
            Authentication authentication
    ){
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        ScheduledActuationId schedId = ScheduledActuationId.fromSerialized(_scheduledActuationId);
        
        if(active){
            scheduledActuationService.activate(principal.getBulbsContextUserlId(), schedId);
        }else{
            scheduledActuationService.deactivate(principal.getBulbsContextUserlId(), schedId);
        }
        
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value="/{scheduledActuationId}/trigger")
    public void setNewTrigger(
            @PathVariable("scheduledActuationId") String _scheduledActuationId,
            @RequestBody @Valid Trigger trigger,
            Authentication authentication
    ){
        BulbsContextUser principal = ((BulbsContextUser)authentication.getPrincipal());
        ScheduledActuationId schedId = ScheduledActuationId.fromSerialized(_scheduledActuationId);

        scheduledActuationService.modifyTrigger(principal.getBulbsContextUserlId(), schedId, trigger);
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value="/{scheduledActuationId}/states",
            consumes = "application/json")
    public void modifyStates(
            @PathVariable("scheduledActuationId") String _scheduledActuationId,
            final @RequestBody @Valid List<AbstractActuatorCmd> newStates,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        ScheduledActuationId schedId = ScheduledActuationId.fromSerialized(_scheduledActuationId);
        
        modelPort.registerConverterFor(DtoScheduledActuation.class);
        
        scheduledActuationService.modifyStates(
                principal.getBulbsContextUserlId(), 
                schedId, 
                newStates);
    }
    

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
