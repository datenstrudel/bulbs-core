package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbBridge;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterBridge implements DtoConverter<BulbBridge, DtoBulbBridge>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoBulbBridge convert(BulbBridge src) {
        DtoBulbBridge res = new DtoBulbBridge();
        res.setBridgeId(src.getId().getUuId());
        res.setName(src.getName());
        res.setLocalAddress(src.getLocalAddress());
        res.setMacAddress(src.getMacAddress());
        res.setPlatform(src.getPlatform());
        res.setOnline(src.isOnline());
        res.setCountBulbs(src.getBulbs().size());
        return res;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    @Override
    public BulbBridge reverseConvert(DtoBulbBridge src) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<DtoBulbBridge> supportedDtoClass() {
        return DtoBulbBridge.class;
    }
    @Override
    public Class<BulbBridge> supportedDomainClass() {
        return BulbBridge.class;
    }

}
