package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupRepository;
import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserDomainService;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepository;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Thomas Wendzinski
 */
@Service("actuatorDomainService")
public class ActuatorDomainServiceImpl implements ActuatorDomainService{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ActuatorDomainServiceImpl.class);
    @Autowired
    private BulbBridgeRepository bridgeRepository;
    @Autowired
    private BulbsContextUserDomainService userService;
    @Autowired
    private BulbGroupRepository groupRepository;
    @Autowired
    private PresetRepository presetRepository;

    @Autowired
    private CounterService counterService;
    
    private static final Map<Integer, AbstractActuatorCmd> deferredCommands 
            = new ConcurrentHashMap<>();
    //~ Construction ///////////////////////////////////////////////////////////

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void executeDeferred(AbstractActuatorCmd command) {
        Assert.notNull(command.getUserApiKey());
        deferredCommands.put(command.deferredExecutionHash(), command);
        counterService.decrement("ws.inbound.receivedButNotProcessed");
    }
    @Override
    public void execute(AbstractActuatorCmd command) throws BulbBridgeHwException{
        command.execute(this);
    }
    @Override
    public void execute(Collection<? extends AbstractActuatorCmd> commands) throws BulbBridgeHwException{
        for (AbstractActuatorCmd el : commands) {
            el.execute(this);
        }
    }

    //~ Command Specific /////////////////////////////////////////////////////
    @Override
    public void execute(BulbActuatorCommand bulbCommand) throws BulbBridgeHwException {
        Assert.notNull(bulbCommand.getUserApiKey());
        BulbBridgeId bridgeId = bulbCommand.getTargetId().getBridgeId();
        BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(
                bulbCommand.getUserApiKey(), bulbCommand.getAppId(), bridgeId);
        
        BulbBridge bridge = bridgeRepository.findOne(bridgeId);
        if(bridge == null){
            log.error("BulbBridge["+bridgeId+"] doesnt' exist! Command not executable.");
            throw new IllegalArgumentException("BulbBridge["+bridgeId+"] doesnt' exist! Command not executable.");
        }
        bridge.execBulbActuation(bulbCommand, principal);
        bridgeRepository.save(bridge);
    }
    @Override
    public void execute(GroupActuatorCommand groupCmd) throws BulbBridgeHwException{
        BulbGroup group = groupRepository.findOne(groupCmd.getTargetId());
        if(group == null){
            throw new IllegalArgumentException("Group["+groupCmd.getTargetId()+"] addressed doesn't exist!");
        }
        List<BulbActuatorCommand> bulbCommands = new ArrayList<>(group.getBulbs().size());
        for (BulbId bulbId : group.getBulbs()) {
            bulbCommands.add(
                    new BulbActuatorCommand(
                            bulbId, 
                            groupCmd.getAppId(), 
                            groupCmd.getUserApiKey(), 
                            groupCmd.getPriority(), 
                            groupCmd.getStates(), 
                            groupCmd.isLoop())
            );
        }
        execute(bulbCommands);
    }
    @Override
    public void execute(PresetActuatorCommand presetCommand)throws BulbBridgeHwException{
        Preset preset = presetRepository.findOne(presetCommand.getTargetId());
        if(preset == null){
            throw new IllegalArgumentException("Preset["+presetCommand.getTargetId()+"] addressed doesn't exist!");
        }
        for (AbstractActuatorCmd el : preset.getStates() ) {
            el.setAppId(presetCommand.getAppId()); 
            el.setUserApiKey(presetCommand.getUserApiKey()); 
            el.setPriority(presetCommand.getPriority()); 
            el.setLoop(presetCommand.isLoop());
        }
        execute(preset.getStates());
    }
    @Override
    public void execute(ActuationCancelCommand cancelCommand) throws BulbBridgeHwException {
        Assert.notEmpty(cancelCommand.getEntityIds(), "EntityIds must not be empty!");
        // AspectJ doesn't compile  :/
//        Map<BulbBridgeId, List<BulbId>> groupedMap = 
//                cancelCommand.getEntityIds().stream().collect(
//                    Collectors.groupingBy( (BulbId t) -> { return t.getId(); } )
//        );
        Map<BulbBridgeId, Set<BulbId>> groupedMap = new HashMap<>();
        cancelCommand.getEntityIds().forEach( (BulbId bId) -> {
            if( !groupedMap.containsKey(bId.getBridgeId()) ){
                groupedMap.put(bId.getBridgeId(), new HashSet<>());
            }
            groupedMap.get(bId.getBridgeId()).add(bId);
        });
        groupedMap.forEach( (BulbBridgeId bridgeId, Set<BulbId> bIds) -> {
            BulbsPrincipal principal = userService.loadPrincipalByUserApiKey(
                    cancelCommand.getUserApiKey(), cancelCommand.getAppId(), bridgeId);

            BulbBridge bridge = bridgeRepository.findOne(bridgeId);
            if(bridge == null){
                log.error("BulbBridge["+bridgeId+"] doesnt' exist! Command not executable.");
                throw new IllegalArgumentException("BulbBridge["+bridgeId+"] doesnt' exist! Command not executable.");
            }
            bridge.cancelActuation(
                new ActuationCancelCommand(
                    new HashSet<>(bIds), 
                    cancelCommand.getAppId(), 
                    cancelCommand.getUserApiKey(),
                    cancelCommand.getPriority()), 
                principal);
       });
    }

    @Scheduled(fixedRate = 200)
    public void publishDeferred() throws BulbBridgeHwException{
//        if(log.isDebugEnabled() && deferredCommands.size() > 0){
//            log.debug("Executing deferred commands.. " + deferredCommands.size());
//        }
        for (Map.Entry<Integer, AbstractActuatorCmd> el : deferredCommands.entrySet()) {
            AbstractActuatorCmd cmd = el.getValue();
            synchronized(deferredCommands){
                try{
                    execute(cmd);
                }finally{
                    deferredCommands.remove(el.getKey());
                }
                
            }
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
