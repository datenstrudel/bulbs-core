package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbSearchFinished;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.infrastructure.Runnable_EventPublishingAware;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbCmdTranslator;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbCmdTranslator_PhilipsHue;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value="bulbsHwService")
public class BulbsHwServiceImpl implements BulbsHwService{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsHwServiceImpl.class);
    @Autowired
    @Qualifier(value = "bulbBridgeHardwareAdapter_REST")
    private BulbBridgeHardwareAdapter hwAdapter_Rest;
    
    @Autowired
    @Qualifier(value = "bulbBridgeHardwareAdapter_Emulated")
    private BulbBridgeHardwareAdapter hwAdapter_Emulated;
    @Autowired
    @Qualifier("taskExecutor")
    private AsyncTaskExecutor asyncExecutor;
    
    private static final Map<Object, CmdHwExecutor> runningExecutions 
            = new ConcurrentHashMap<>();

    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ BRIDGE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public BulbBridge bridgeFromHwInterface(
            BulbBridgeAddress address, 
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).toBridge(address, bridgeId, principal, contextUserId, 
                translatorForPlatform(platform) );
    }
    @Override 
    public void discoverNewBulbs(
            final BulbBridgeId bulbBridgeId, 
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final BulbsPlatform platform) throws BulbBridgeHwException {
        hwAdapterForPlatform(platform).discoverNewBulbs(
                address, principal, translatorForPlatform(platform));
        asyncExecutor.execute(new Runnable_EventPublishingAware() {
            @Override
            public void execute() {
                // We wait, as underlying hardware processes the search.
                try{
                    Thread.sleep(60000l);
                }catch(InterruptedException iex){}
                DomainEventPublisher.instance().publish(
                        new BulbSearchFinished(bulbBridgeId.getUuId(), principal));
            }
        } );
    }
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).modifyBridgeAttributes(address, principal, attributes, translatorForPlatform(platform));
    }

    @Override
    public Set<BulbsPrincipal> bulbsPrincipalsFromHwInterface(
            BulbBridge bridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).toBulbsPrincipals(bridge, principal, translatorForPlatform(platform));
    }
    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).createBulbsPrincipal(address, principal, translatorForPlatform(platform));
    }
    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).removeBulbsPrincipal(address, principal, principal2Remove, translatorForPlatform(platform));
    }

    //~ BULBs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public BulbId[] bulbIdsFromHwInterface(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).toBulbIds(parentBridge, principal, translatorForPlatform(platform));
    }
    
    @Override
    public Bulb[] bulbsFromHwInterface(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).toBulbs(parentBridge, principal, translatorForPlatform(platform));
    }

    @Override
    public Bulb bulbFromHwInterface(
            BulbId bulbId, 
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).toBulb(bulbId, parentBridge, principal, translatorForPlatform(platform));
    }
    
    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbsPlatform platform) throws BulbBridgeHwException {
        return hwAdapterForPlatform(platform).modifyBulbAttributes(
                bulbId, address, principal, attributes, translatorForPlatform(platform));
    }
    
    @Override
    public void cancelActuation(BulbId id){
        CmdHwExecutor exec = this.runningExecutions.get(id);
        if(exec == null)return ;
        exec.cancelExecution();
    }
            
    @Override
    public void executeBulbActuation(
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final BulbActuatorCommand command, 
            final BulbsPlatform platform) throws BulbBridgeHwException {
        if(command.getStates().isEmpty())return;
        
        CmdHwExecutor exec = new CmdHwExecutor(
                address, principal, command, 
                hwAdapterForPlatform(platform), 
                translatorForPlatform(platform));
        CmdHwExecutor runningExec;
        if( ( runningExec = this.runningExecutions.get( command.getBulbId()) ) != null ){
            runningExec.cancelExecution();
        }
        this.runningExecutions.put(command.getBulbId(), exec);
        
        asyncExecutor.execute(exec, 50l);
        
    }
    
    @Scheduled(fixedRate = 2000)
    public void clearFinishedFromIndex(){
        if( runningExecutions.isEmpty() )return ;
        runningExecutions.entrySet().removeIf(
                (Entry<Object, CmdHwExecutor> t) -> {
            return t.getValue().isExecutionFinished();
        });
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private BulbBridgeHardwareAdapter hwAdapterForPlatform(BulbsPlatform platform){
        switch(platform){
            case _EMULATED:
                return hwAdapter_Emulated;
            case PHILIPS_HUE:
                return hwAdapter_Rest;
            default:
                throw new UnsupportedOperationException("Platform not supported: " + platform);
        }
        
    }
    private BulbCmdTranslator translatorForPlatform(BulbsPlatform platform){
        switch(platform){
            case _EMULATED:
                return null; // No translator necessary
            case PHILIPS_HUE:
                return new BulbCmdTranslator_PhilipsHue();
            default:
                throw new UnsupportedOperationException("Platform not supported: " + platform);
        }
    }
    


    
}