package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbBridgeDeleted;
import net.datenstrudel.bulbs.core.domain.model.identity.*;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.shared.domain.model.bulb.AppLinkResult;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Service(value="bulbBridgeDomainService")
public class BulbBridgeDomainServiceImpl implements BulbBridgeDomainService{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeDomainServiceImpl.class);
    
    @Autowired
    private BulbsContextUserDomainService userService;
    private BulbsContextUserRepository userRepository;
    @Autowired
    private BulbBridgeRepository bridgeRepository;
    @Autowired
    private BulbsHwService bulbsHwService;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbBridgeDomainServiceImpl() {
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    @Deprecated
    public AppLinkResult linkAppToCore(String userApiKey, AppId appId)
            throws IllegalArgumentException{
        BulbsContextUser user = userRepository.findByApiKey(userApiKey);
        String apiKey;
        AppLinkResult res;
        if(user == null) throw new IllegalArgumentException("User with given apiKey["
                + userApiKey + "] doesn't exist!");

        Set<BulbBridge> bridges = bridgeRepository.findByOwner(
                user.getBulbsContextUserlId());
        apiKey = user.getApiKey();
        res = new AppLinkResult(apiKey);
        
        if(bridges.isEmpty())return res;
        
        //~ Register app specific principals to each bridge if not present yet
        BulbsPrincipal tmpPrincipal;
        String uvBridgeId;
        for (BulbBridge el : bridges) {
            uvBridgeId = el.getId().getUuId() + "-" + el.getName();
            tmpPrincipal = user.principalFrom(el.getId());
            if(tmpPrincipal != null){
                res.addLinkState(uvBridgeId, true);
                continue;
            }
            tmpPrincipal = userService.bulbsPrincipalInstanceForNewBridge(appId);
            try {
                el.registerBulbsPrincipal(tmpPrincipal, user);
                res.addLinkState(uvBridgeId, true);
            } catch (BulbBridgeHwException ex) {
                log.warn(ex.getMessage(), ex);
                res.addLinkState(uvBridgeId, false);
                res.addLinkError(uvBridgeId, ex.getLocalizedMessage());
            }
        }
        return res;
    }
    
    @Override
    public BulbBridge findAndCreateBulbBridge(
            BulbBridgeAddress localAddress, 
            BulbsPlatform platform, 
            BulbsContextUser user
            ) throws BulbBridgeHwException{
        
        log.info("|-- Going to create new BulbBridge, looking for address "+localAddress);
        BulbsPrincipal finalPrincipal;
        BulbsPrincipal tmpPrincipal = userService
                .bulbsPrincipalInstanceForNewBridge( AppIdCore.instance() );

        log.info(" -- Try to create new " + tmpPrincipal + "at given address .. ");
        InvocationResponse resp_principal = bulbsHwService.createBulbsPrincipal(localAddress, tmpPrincipal, platform);
        log.info(" -- .. SUCCEEDED.");

        final Optional<String> createdUsername = resp_principal.getValueFromParsedResponse("username", String.class);
        createdUsername.ifPresent(tmpPrincipal::setUsername);

            log.info(" -- Try to read bridge data from hardware ..");
        BulbBridge freshBridge = bulbsHwService.bridgeFromHwInterface(
                localAddress, bridgeRepository.nextIdentity(), tmpPrincipal, user.getBulbsContextUserlId(), platform);
        log.info(" -- .. Object read: " + freshBridge);
        log.info(" -- .. SUCCEEDED.");
        
        log.info(" -- Try to sync bridge hardware state to new BulbBridge object ..");
        freshBridge.syncToHardwareState(tmpPrincipal);
        log.info(" -- .. SUCCEEDED.");
        
        BulbBridge existingBridge = bridgeRepository.findOne(freshBridge.getId());
        if(existingBridge != null) throw new IllegalArgumentException("Bridge with ID["
                +freshBridge.getId()+"] already in use and cannot be assigned a second time!");
        
        finalPrincipal = new BulbsPrincipal(
                tmpPrincipal.getUsername(), 
                tmpPrincipal.getAppId(), 
                freshBridge.getId().getUuId(),
                BulbsPrincipalState.PENDING);
        
        log.info(" -- Try to persistently save new BulbBridge .. ");
        freshBridge = bridgeRepository.save(freshBridge);
        log.info(" -- SUCCEEDED (still before commit). ");
        
        DomainEventPublisher.instance().publish(
                new BulbsPrincipalInitLinkEstablished(
                    user.getBulbsContextUserlId().getUuid(),
                    finalPrincipal));
        
        return freshBridge;
    }
    
    @Override
    public void removeBulbBridge(BulbBridgeId bridgeId, BulbsContextUser initiator) {
        BulbBridge b2Del = bridgeRepository.findOne(bridgeId);
        if(b2Del == null) throw new IllegalArgumentException("Cannot delete Bridge["+bridgeId+"] due to doesn't exist.");
        if(!b2Del.getOwner().sameValueAs(initiator.getBulbsContextUserlId())){
            throw new IllegalArgumentException("Cannot delete Bridge["+bridgeId
                    +"] by user["+initiator.getBulbsContextUserlId()
                    +"] due to currently only the owner of a bridge is supported to trigger deletion.");
        }
        bridgeRepository.delete(bridgeId);
        
        DomainEventPublisher.instance().publish( new BulbBridgeDeleted(
                initiator.getBulbsContextUserlId(), 
                bridgeId,
                b2Del.getPlatform(),
                b2Del.getLocalAddress()));
    }

    public void removeBulbsPrincipalsAfterDeletion(
            BulbsPrincipal principal,
            BulbsPrincipal[] principals2Remove,
            BulbBridgeAddress bridgeAddress,
            BulbsPlatform platform,
            BulbsContextUser user ) throws BulbBridgeHwException{

        boolean deleteInvocerToo = false;
        for(BulbsPrincipal el : principals2Remove){
            if(el.equals(principal)){
                deleteInvocerToo = true;
                continue;
            }
            bulbsHwService.removeBulbsPrincipal(bridgeAddress, principal, el, platform);
            DomainEventPublisher.instance().publish(
                    new BulbsPrincipalDeletedFromBridge(el, user.getBulbsContextUserlId().getUuid())
            );
        }
        if(deleteInvocerToo){
            bulbsHwService.removeBulbsPrincipal(bridgeAddress, principal, principal, platform);
            DomainEventPublisher.instance().publish(
                    new BulbsPrincipalDeletedFromBridge(principal, user.getBulbsContextUserlId().getUuid())
            );
        }

    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
