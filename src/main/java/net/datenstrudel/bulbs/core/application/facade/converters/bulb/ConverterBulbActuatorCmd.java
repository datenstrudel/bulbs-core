package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterBulbActuatorCmd 
        implements DtoConverter<BulbActuatorCommand, DtoBulbActuatorCmd> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoBulbActuatorCmd convert(BulbActuatorCommand src) {
        DtoBulbActuatorCmd out = new DtoBulbActuatorCmd();
        out.setBulbId(src.getBulbId().serialize());
        out.setAppId(src.getAppId().getUniqueAppName());
        out.setPriority(src.getPriority());
        out.setStates(src.getStates());
        out.setLoop(src.isLoop());
        return out;
    }
    @Override
    public BulbActuatorCommand reverseConvert(DtoBulbActuatorCmd in) {
        BulbActuatorCommand res = new BulbActuatorCommand(
                BulbId.fromSerialized(in.getBulbId()),
                new AppId(in.getAppId()),
                null,
                in.getPriority(),
                in.getStates(), 
                in.isLoop()
            );
        return res;
    }
    
    @Override
    public Class<DtoBulbActuatorCmd> supportedDtoClass() {
        return DtoBulbActuatorCmd.class;
    }
    @Override
    public Class<BulbActuatorCommand> supportedDomainClass() {
        return BulbActuatorCommand.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
