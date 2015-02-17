package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.*;
import net.datenstrudel.bulbs.core.domain.model.messaging.IllegalArgumentExceptionNotRepeatable;
import net.datenstrudel.bulbs.core.web.controller.util.AuthenticationException;
import net.datenstrudel.bulbs.core.web.controller.util.NotAuthorizedException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.*;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Application service for administrative bridge/coordination/master entity functionality
 * @author Thomas Wendzinski
 */
@Service(value="bulbBridgeAdminService")
public class BulbBridgeAdminServiceImpl 
        implements BulbBridgeAdminService, IBulbBridgeAdminServiceInternal{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeAdminServiceImpl.class);
    @Autowired
    private BulbsContextUserRepository userRepository;
    @Autowired
    private BulbsContextUserDomainService userService;
    @Autowired
    private BulbBridgeDomainService bridgeDomainService;
    @Autowired
    private BulbBridgeRepository bridgeRepository;
    @Autowired 
    private ModelFacadeOutPort modelPort;
    
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public AppLinkResult linkAppToCore(String userApiKey, AppId appId) {
        log.info("|-- Try to link App["+appId+"] for user with apiKey["+userApiKey+"]");
        AppLinkResult res = bridgeDomainService.linkAppToCore(userApiKey, appId);
        log.info(" -- linking App["+appId+"] succeeded.");
        return res;
    }
    
    @Override
    public BulbBridge findAndCreateBulbBridge(
            BulbsPlatform platform, 
            BulbBridgeAddress localAddress, 
            String userApiKey)throws BulbBridgeHwException {
        
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new IllegalArgumentException("No user for apiKey["+userApiKey+"]");
        BulbBridge res = bridgeDomainService.findAndCreateBulbBridge(
                localAddress, platform, user);
        modelPort.write(res);
        
        return res;
    }
    @Override
    public void performBulbSearch(BulbBridgeId bridgeId, String userApiKey) 
            throws BulbBridgeHwException{
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        BulbBridge bridge = bridgeRepository.findOne(bridgeId);
        if(!bridge.getOwner().sameValueAs(user.getBulbsContextUserlId()))
            throw new NotAuthorizedException("Permission denied for ApiKey["+userApiKey+"] to access bridge with "+bridgeId);
        bridge.performBulbSearch(user.principalFrom(bridgeId));
    }

    @Override
    public void syncAllBridges(String userApiKey) throws BulbBridgeHwException {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        Set<BulbBridge> bridges = bridgeRepository.findByOwner(user.getBulbsContextUserlId());
        for (BulbBridge el : bridges) {
            try{
                el.syncToHardwareState(user.principalFrom(el.getId()));
            }catch(BulbBridgeHwException bbhwex){
                log.info("Couldn't sync due to: " + bbhwex.getMessage());
                el.markRecursivelyOffline(user.principalFrom(el.getId()));
            }
            bridgeRepository.save(el);
        }
    }
    
    @Override
    public Bulb loadBulb(BulbId bulbId, String userApiKey){
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        BulbBridge bridge = bridgeRepository.findOne(bulbId.getBridgeId());
        if(!bridge.getOwner().sameValueAs(user.getBulbsContextUserlId()))
            throw new NotAuthorizedException("Permission denied for ApiKey["+userApiKey+"] to access bulb with " + bulbId);
        Bulb res = bridge.bulbById(bulbId);
        if(res == null)throw new IllegalArgumentException("Bulb["+bulbId+"] doesn't exist.");
        modelPort.write(res);
        return res;
    }
    @Override
    public Set<Bulb> bulbsByContextUser(String userApiKey) {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        Set<BulbBridge> bridges = bridgeRepository.findByOwner(user.getBulbsContextUserlId());
        Set<Bulb> bulbs = new HashSet<>();
        for (BulbBridge bulbBridge : bridges) {
            bulbs.addAll(bulbBridge.getBulbs());
        }
        modelPort.write(bulbs);
        return bulbs;
        
    }

    @Override
    public BulbBridge loadBridge(BulbBridgeId bridgeId, String userApiKey) {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        BulbBridge res = bridgeRepository.findOne(bridgeId);
        if(!res.getOwner().sameValueAs(user.getBulbsContextUserlId()))
            throw new IllegalArgumentException("Permission denied for ApiKey["+userApiKey+"] to access bridge with "+bridgeId);
        modelPort.write( res);
        return res;
    }
    @Override
    public Set<BulbBridge> bridgesByContextUser(String userApiKey) {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        Set<BulbBridge> res = bridgeRepository.findByOwner(user.getBulbsContextUserlId());
        modelPort.write( (Collection) res);
        return res;
    }
    @Override
    public String[] allBulbNames(String userApiKey) {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new AuthenticationException("No user for apiKey["+userApiKey+"]");
        Set<BulbBridge> bridges = bridgeRepository.findByOwner(user.getBulbsContextUserlId());
        List<String> res = new ArrayList<>();
        for (BulbBridge el : bridges) {
            res.addAll(el.allBulbNames());
        }
        return res.toArray(new String[res.size()]);
    }
    
    @Override
    public void modifyBridgeName(
            String userApiKey, 
            BulbBridgeId bridgeId, 
            String newName)throws BulbBridgeHwException {
        
        BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(
                userApiKey, AppIdCore.instance(), bridgeId);
        BulbBridge b = bridgeRepository.findOne(bridgeId);
        
        if(b == null) throw new IllegalArgumentException("Bridge["+bridgeId+"] doesn't exist.");
        b.modifyName(newName, principal);
        bridgeRepository.save(b);
    }
     
    @Override
    public void modifyBulbName(
            String userApiKey, 
            BulbId bulbId,
            String newName)throws BulbBridgeHwException {
        BulbBridgeId bridgeId = bulbId.getBridgeId();
        BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(
                userApiKey, AppIdCore.instance(), bridgeId);
        BulbBridge b = bridgeRepository.findOne(bridgeId);
        
        if(b == null) throw new IllegalArgumentException("Bulb with ["+bulbId+"] doesn't exist.");
        b.mofiyBulbName(principal, bulbId, newName);
        bridgeRepository.save(b);
    }
    
    @Override
    public void modifyLocalAddress(
            String userApiKey,
            BulbBridgeId bridgeId, 
            BulbBridgeAddress newAddress) throws BulbBridgeHwException{
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new IllegalArgumentException("No user for apiKey["+userApiKey+"]");
        BulbBridge b = bridgeRepository.findOne(bridgeId);
        if(b == null) throw new IllegalArgumentException("Bridge["+bridgeId+"] doesn't exist.");
        BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(
                userApiKey, AppIdCore.instance(), bridgeId);
        b.modifyLocalAddress(newAddress, principal);
        bridgeRepository.save(b);
    }

    public void removeAllBulbsPrincipalsAfterBridgeDeletion(
            final BulbsContextUserId userId, final BulbBridgeId bridgeId, BulbBridgeAddress address, BulbsPlatform platform) {

        log.info("|-- Going to delete BulbsPrincipals from  hardware at address["+address+"]");
        final BulbsContextUser user = userRepository.findOne(userId);
        BulbsPrincipal[] principals2Del = user.getBulbsPrincipals().stream()
                .filter(bulbsPrincipal -> bulbsPrincipal.getBulbBridgeId().equals(bridgeId.getUuId()))
                .toArray(value -> new BulbsPrincipal[value] );
        // TODO: Determine BulbsPrincipal objects from other users, too, in order to remove them!
        if(principals2Del.length < 1 )return;

        try {
            this.bridgeDomainService.removeBulbsPrincipalsAfterDeletion(
                   principals2Del[0], principals2Del, address, platform, user
            );
        } catch (BulbBridgeHwException e) {
            if(e.getStatusCode() == null ) throw new RuntimeException("Hardware was not reachable", e);
            else throw new IllegalArgumentExceptionNotRepeatable("Principal couldn't be deleted from bridge: " + e.getMessage());
        }
    }

    @Override
    public void removeBulbsPrincipal(
            String userApiKey,
            BulbBridgeId bulbBridgeId, 
            BulbsPrincipal principal2Remove) throws BulbBridgeHwException {
        BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(userApiKey, AppIdCore.instance(), bulbBridgeId);
        BulbBridge b = bridgeRepository.findOne(bulbBridgeId);
        if(b == null) throw new IllegalArgumentException("Bridge["+bulbBridgeId+"] doesn't exist.");
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        b.removeBulbsPrincipal(principal, principal2Remove, user);
        bridgeRepository.save(b);
    }
    
    @Override
    public void removeBulbBridge(
            String userApiKey, 
            BulbBridgeId bridgeId ) {
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        if(user == null) throw new IllegalArgumentException("No user for apiKey["+userApiKey+"]");
        bridgeDomainService.removeBulbBridge(bridgeId, user);
    }
    
    //~ By IBulbBridgeAdminServiceInternal /////////////////////////////////////
    @Override
    public void updateBulbStateInternal(BulbId bulbId, BulbState state) {
        BulbBridge bridge = bridgeRepository.findOne(bulbId.getBridgeId());
        if(bridge == null) throw new IllegalArgumentExceptionNotRepeatable(
                "Bridge didn't exist anymore: " + bulbId.getBridgeId());
        Bulb bulb = bridge.bulbById(bulbId);
        if(bulb == null) throw new IllegalArgumentExceptionNotRepeatable(
                "Bulb didn't exist anymore: " + bulbId );
        bulb.updateState(state);
        bridgeRepository.save(bridge);
    }
    @Override
    public void syncToHardwareStateInternal(BulbBridgeId bridgeId, BulbsPrincipal principal) {
        log.info("Going to sync hardware state for bridge " + bridgeId);
        BulbBridge bridge = bridgeRepository.findOne(bridgeId);
        if(bridge == null) throw new IllegalArgumentExceptionNotRepeatable(
                "Bridge didn't exist anymore: " + bridgeId);
        try{
            bridge.syncToHardwareState(principal);
            bridgeRepository.save(bridge);
        }catch(BulbBridgeHwException bbhex){
            log.error(bbhex.getMessage(), bbhex);
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
