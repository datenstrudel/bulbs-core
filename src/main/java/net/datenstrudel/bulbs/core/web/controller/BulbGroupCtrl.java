package net.datenstrudel.bulbs.core.web.controller;

import com.wordnik.swagger.annotations.Api;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.BulbGroupService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.ValidatorBulbGroup;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.model.client.group.DtoGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@RestController
@RequestMapping(value = "/core/")
@Api(value = "groups", description = ".. contain bulbs", position = 30)
public class BulbGroupCtrl {

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired 
    private ModelFacadeOutPort modelPort;
    @Autowired
    private DtoConverterRegistry converterFactory;
    @Autowired 
    BulbGroupService groupService;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @RequestMapping(method = RequestMethod.GET, value="groups/{groupId}")
    public DtoGroup loadById(
            @PathVariable("groupId") String groupId,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        BulbGroupId grpId = BulbGroupId.fromSerialized(groupId);
        
        modelPort.registerConverterFor(DtoGroup.class);
        groupService.loadById(principal.getBulbsContextUserlId(), grpId);
        return modelPort.outputAs(DtoGroup.class);
    }
    @RequestMapping(method = RequestMethod.GET, value="groups")
    public Set<DtoGroup> groupsByUser(
            Authentication authentication
    ){
        
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        modelPort.registerConverterFor(DtoGroup.class);
        groupService.loadAllByUser(principal.getBulbsContextUserlId());
        return modelPort.outputAsSet(DtoGroup.class);
    }
    
    @RequestMapping(method = RequestMethod.POST, value="groups")
    public DtoGroup create(
            final @RequestBody DtoGroup dtoNewGroup,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        
        modelPort.registerConverterFor(DtoGroup.class);
        groupService.createNew(principal.getBulbsContextUserlId(), 
                dtoNewGroup.getName(), new ValidatorBulbGroup.NotificationHandlerBulbGroup() {
            @Override
            public void handleDuplicateGroupname() {
                throw new IllegalArgumentException("Group name "+dtoNewGroup.getName()+" already in use!");
            }
        });
        return modelPort.outputAs(DtoGroup.class);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="groups/{groupId}/bulbIds")
    public DtoGroup modifyAllGroupMembers(
            @PathVariable("groupId") String groupId,
            final @RequestBody DtoGroup modifiedGroup,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        BulbGroupId grpId = BulbGroupId.fromSerialized(groupId);
        
        modelPort.registerConverterFor(DtoGroup.class);
        groupService.modifyGroupMembers(
                principal.getBulbsContextUserlId(), grpId,
                (Collection<BulbId>) converterFactory.converterForDomain(BulbId.class)
                        .reverseConvertCollection((Collection) modifiedGroup.getBulbIds())
        );
        return modelPort.outputAs(DtoGroup.class);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value="groups/{groupId}")
    public DtoGroup modifyGroupName(
            @PathVariable("groupId") String groupId,
            final @RequestBody DtoGroup modifiedGroup,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        BulbGroupId grpId = BulbGroupId.fromSerialized(groupId);
        
        modelPort.registerConverterFor(DtoGroup.class);
        groupService.modifyName(
                principal.getBulbsContextUserlId(), 
                grpId, 
                modifiedGroup.getName(), 
                new ValidatorBulbGroup.NotificationHandlerBulbGroup() {
                    @Override
                    public void handleDuplicateGroupname() {
                        throw new IllegalArgumentException("Group name "+modifiedGroup.getName()+" already in use!");
                    } 
                }
        );
        return modelPort.outputAs(DtoGroup.class);
    }

    @RequestMapping(method = RequestMethod.DELETE, value="groups/{groupId}")
    public void deleteGroup(
            @PathVariable("groupId") String groupId,
            Authentication authentication
    ){
        BulbsContextUser principal =  ((BulbsContextUser)authentication.getPrincipal());
        BulbGroupId grpId = BulbGroupId.fromSerialized(groupId);
        
        groupService.remove(principal.getBulbsContextUserlId(), grpId);
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
