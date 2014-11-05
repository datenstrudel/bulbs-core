package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeRepository;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
@Service(value="bulbsContextUserDomainService")
public class BulbsContextUserDomainServiceImpl implements BulbsContextUserDomainService {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserDomainServiceImpl.class);
    @Autowired
    private BulbsContextUserRepository userRepository;
    @Autowired
    private BulbBridgeRepository bulbBrideRepository;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbsPrincipal loadPrincipalByUserApiKey(
            String userApiKey, AppId appId, BulbBridgeId bridgeId){
        BulbsContextUser user = userRepository.loadByApiKey(userApiKey);
        if(user == null)throw new IllegalArgumentException("No user for apiKey["+userApiKey+"]");
        BulbsPrincipal principal = user.principalFrom(bridgeId);
        if(principal == null)throw new IllegalArgumentException("User with apiKey["+userApiKey+"] not linked with app["+appId+"]");
        return principal;
    }
    
    @Override
    public BulbsPrincipal bulbsPrincipalInstanceForNewBridge(AppId appId) {
        String username = UUID.randomUUID().toString();
        BulbsPrincipal p = new BulbsPrincipal(username, appId, null, BulbsPrincipalState.REQUESTED);
        return p;
    }
    
    @Override
    public String createNewApiKey(){
        //TODO: Hash new key!
        String res = UUID.randomUUID().toString().toUpperCase();
        return res;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}

