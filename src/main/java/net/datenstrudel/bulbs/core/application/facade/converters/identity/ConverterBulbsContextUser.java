package net.datenstrudel.bulbs.core.application.facade.converters.identity;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.model.client.identity.DtoBulbsContextUser;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterBulbsContextUser 
        implements DtoConverter<BulbsContextUser, DtoBulbsContextUser>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoBulbsContextUser convert(BulbsContextUser src) {
        DtoBulbsContextUser res = new DtoBulbsContextUser();
        res.setApiKey(src.getApiKey());
        res.setEmail(src.getEmail());
        res.setNickname(src.getNickname());
        
        return res;
    }
    @Override
    public BulbsContextUser reverseConvert(DtoBulbsContextUser src) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<DtoBulbsContextUser> supportedDtoClass() {
        return DtoBulbsContextUser.class;
    }
    @Override
    public Class<BulbsContextUser> supportedDomainClass() {
        return BulbsContextUser.class;
    }
    
    

}
