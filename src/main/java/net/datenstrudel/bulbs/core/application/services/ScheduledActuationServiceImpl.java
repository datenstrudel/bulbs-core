/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserRepository;
import net.datenstrudel.bulbs.core.domain.model.messaging.IllegalArgumentExceptionNotRepeatable;
import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepository;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Service
public class ScheduledActuationServiceImpl implements ScheduledActuationService{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationServiceImpl.class);
    private final ScheduledActuationRepository schedRepo;
    private final BulbsContextUserRepository userRepository;
    private final ModelFacadeOutPort outPort;
    private final JobCoordinator jobCoordinator;
    private final ActuatorService actuatorService;

    //~ Construction ///////////////////////////////////////////////////////////
    @Autowired
    public ScheduledActuationServiceImpl(
            ScheduledActuationRepository schedRepo, 
            ModelFacadeOutPort outPort,
            JobCoordinator jobCoordinator,
            ActuatorService actuatorService,
            BulbsContextUserRepository userRepository
    ) {
        this.schedRepo = schedRepo;
        this.outPort = outPort;
        this.jobCoordinator = jobCoordinator;
        this.actuatorService = actuatorService;
        this.userRepository = userRepository;
    }
    
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public Set<ScheduledActuation> loadAllByUser(BulbsContextUserId userId) {
        Set<ScheduledActuation> res = schedRepo.loadByOwner(userId);
        outPort.write(res);
        return res;
    }
    @Override
    public ScheduledActuation loadById(BulbsContextUserId userId, ScheduledActuationId id) {
        ScheduledActuation res = schedRepo.loadById(id);
        outPort.write(res);
        return res;
    }
    

    @Override
    public ScheduledActuation create(BulbsContextUserId userId, String name, boolean removeAfterExecution) {
        ScheduledActuation res = new ScheduledActuation(
                schedRepo.nextIdentity(userId), name, removeAfterExecution);
        schedRepo.store(res);
        outPort.write(res);
        //TODO: Fire created event        
        return res;
    }
    @Override
    public ScheduledActuation create(BulbsContextUserId userId, String name, Trigger trigger, boolean removeAfterExecution) {
        ScheduledActuation res = new ScheduledActuation(
                schedRepo.nextIdentity(userId), name, removeAfterExecution);
        res.defineTrigger(trigger, jobCoordinator);
        schedRepo.store(res);
        outPort.write(res);
        //TODO: Fire created event        
        return res;
    }
    @Override
    public ScheduledActuation createAndActivate(
            BulbsContextUserId userId, 
            String name, 
            Trigger trigger,
            boolean removeAfterExecution, 
            Collection<AbstractActuatorCmd> states) {
        log.info("User "+ userId +" creates Scheduled Actuation with Trigger: " + trigger + "}");
        
        ScheduledActuation res = new ScheduledActuation(
                schedRepo.nextIdentity(userId), name, removeAfterExecution);
        res.defineTrigger(trigger, jobCoordinator);
        res.setNewStates(states, jobCoordinator);
        res.activate(jobCoordinator);
        schedRepo.store(res);
        outPort.write(res);
        //TODO: Fire created event        
        return res;
    }
    
    @Override
    public void remove(BulbsContextUserId userId, ScheduledActuationId actId) {
        ScheduledActuation entity2Del = schedRepo.loadById(actId);
        if(entity2Del == null){
            throw new IllegalArgumentException("error.scheduledActuation.notFound");
        }
        entity2Del.deActivate(jobCoordinator);
        schedRepo.remove(actId);
    }
    @Override
    public void activate(BulbsContextUserId userId, ScheduledActuationId actId) {
        ScheduledActuation entity = schedRepo.loadById(actId);
        if(entity == null){
            throw new IllegalArgumentException("error.scheduledActuation.notFound");
        }
        entity.activate(jobCoordinator);
        schedRepo.store(entity);
    }
    @Override
    public void deactivate(BulbsContextUserId userId, ScheduledActuationId actId) {
        ScheduledActuation entity = schedRepo.loadById(actId);
        if(entity == null){
            throw new IllegalArgumentException("error.scheduledActuation.notFound");
        }
        entity.deActivate(jobCoordinator);
        schedRepo.store(entity);
    }
    
    @Override
    public void modifyName(BulbsContextUserId userId, ScheduledActuationId actId, String newName) {
        
    }
    @Override
    public void modifyStates(BulbsContextUserId userId, ScheduledActuationId actId, Collection<AbstractActuatorCmd> newStates) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public void execute(BulbsContextUserId userId, ScheduledActuationId actId) {
        ScheduledActuation schedAct = schedRepo.loadById(actId);
        if(schedAct == null)
            throw new IllegalArgumentExceptionNotRepeatable("error.scheduledActuation.notFound");
        
        final String userApiKey = userRepository.loadById(userId).getApiKey();
        final List<AbstractActuatorCmd> states = schedAct.getStates();
        for (AbstractActuatorCmd cmd : states) {
            cmd.setUserApiKey(userApiKey);
        }
        
        try {
            actuatorService.execute(states);
        } catch (BulbBridgeHwException ex) {
            log.error("Error on execution of ScheduledActuation.", ex);
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    

}
