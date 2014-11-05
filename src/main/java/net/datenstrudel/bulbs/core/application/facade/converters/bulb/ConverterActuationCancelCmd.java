package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.ActuationCancelCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoActuationCancelCmd;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterActuationCancelCmd implements 
        DtoConverter<ActuationCancelCommand, DtoActuationCancelCmd> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoActuationCancelCmd convert(ActuationCancelCommand src) {
        DtoActuationCancelCmd out = new DtoActuationCancelCmd();
        out.setEntityIds(new HashSet(DtoConverterRegistry.instance().converterForDomain(BulbId.class)
                .convertCollection(src.getEntityIds())));
        out.setAppId(src.getAppId().getUniqueAppName());
        out.setPriority(src.getPriority());
        out.setStates(src.getStates());
        out.setLoop(src.isLoop());
        return out;
    }
    @Override
    public ActuationCancelCommand reverseConvert(DtoActuationCancelCmd src) {
        Set<BulbId> deviceIds = new HashSet<>(src.getEntityIds().size());
        src.getEntityIds().stream().forEach( deviceId -> deviceIds.add(BulbId.fromSerialized(deviceId)));
        return new ActuationCancelCommand(
                deviceIds,
                new AppId(src.getAppId()),
                null,
                src.getPriority()
        );
    }

    @Override
    public Class<DtoActuationCancelCmd> supportedDtoClass() {
        return DtoActuationCancelCmd.class;
    }
    @Override
    public Class<ActuationCancelCommand> supportedDomainClass() {
        return ActuationCancelCommand.class;
    }

    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
