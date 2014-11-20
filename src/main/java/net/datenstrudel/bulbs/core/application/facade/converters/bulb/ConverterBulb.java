package net.datenstrudel.bulbs.core.application.facade.converters.bulb;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulb;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbBridge;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterBulb implements DtoConverter<Bulb, DtoBulb>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoBulb convert(Bulb src) {
        DtoBulb res = new DtoBulb();
        res.setName(src.getName());
        res.setBulbId(src.getId().serialize());
        res.setOnline(src.getOnline());
        res.setState(src.getState());
        res.setBridge( 
                DtoConverterRegistry.instance()
                .converterFor(DtoBulbBridge.class).convert(src.getBridge())
        );
        return res;
    }
    
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Override
    public Bulb reverseConvert(DtoBulb src) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public Class<DtoBulb> supportedDtoClass() {
        return DtoBulb.class;
    }
    @Override
    public Class<Bulb> supportedDomainClass() {
        return Bulb.class;
    }

}
