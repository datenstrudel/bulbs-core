package net.datenstrudel.bulbs.core.application.facade.converters.group;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.shared.domain.model.client.group.DtoGroupActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterGroupActuatorCmd 
        implements DtoConverter<GroupActuatorCommand, DtoGroupActuatorCmd> {

    //~ Member(s) //////////////////////////////////////////////////////////////

    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoGroupActuatorCmd convert(GroupActuatorCommand src) {
        DtoGroupActuatorCmd out = new DtoGroupActuatorCmd();
        out.setAppId(src.getAppId().getUniqueAppName());
        out.setPriority(src.getPriority());
        out.setStates(src.getStates());
        out.setGroupId(
                (String) DtoConverterRegistry.instance().converterForDomain(BulbGroupId.class)
                        .convert(src.getGroupId()));
        return out;
    }
    @Override
    public GroupActuatorCommand reverseConvert(DtoGroupActuatorCmd in) {
        GroupActuatorCommand res = new GroupActuatorCommand(
                DtoConverterRegistry.instance().converterForDomain(BulbGroupId.class).reverseConvert(in.getGroupId()),
                new AppId(in.getAppId()),
                null,
                in.getPriority(),
                in.getStates(), in.isLoop());
        return res;
    }
    
    @Override
    public Class<DtoGroupActuatorCmd> supportedDtoClass() {
        return DtoGroupActuatorCmd.class;
    }
    @Override
    public Class<GroupActuatorCommand> supportedDomainClass() {
        return GroupActuatorCommand.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
