package net.datenstrudel.bulbs.core.application.facade.converters.group;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.shared.domain.model.client.group.DtoGroup;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterGroup implements DtoConverter<BulbGroup, DtoGroup>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoGroup convert(BulbGroup src) {
        DtoGroup res = new DtoGroup();
        res.setName(src.getName());
        res.setGroupId(
                (String) DtoConverterRegistry.instance()
                        .converterForDomain(BulbGroupId.class).convert(src.getGroupId())
        );
        res.setBulbIds( (Set) DtoConverterRegistry.instance()
                        .converterForDomain(BulbId.class).convertCollection(src.getBulbs()));
        
        return res;
    }
    @Override
    public BulbGroup reverseConvert(DtoGroup src) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<DtoGroup> supportedDtoClass() {
        return DtoGroup.class;
    }
    @Override
    public Class<BulbGroup> supportedDomainClass() {
        return BulbGroup.class;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
