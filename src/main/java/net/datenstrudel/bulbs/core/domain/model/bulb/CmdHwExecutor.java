package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbStateChanged;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.infrastructure.Runnable_EventPublishingAware;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbCmdTranslator;
import net.datenstrudel.bulbs.core.infrastructure.services.bulb.BulbCmdTranslator_HTTP;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Supposed to be executed within separate thread in order to apply hardware states to 
 * hardware interface. Moreover state transitions may be calculated prior execution.
 *
 * @author Thomas Wendzinski
 */
public class CmdHwExecutor extends Runnable_EventPublishingAware {
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(CmdHwExecutor.class);
    private static final int INTERPOLATION_RESOLUTION = 200; // ms
    final BulbBridgeAddress address;
    final BulbsPrincipal principal;
    final BulbActuatorCommand command;
    final BulbBridgeHardwareAdapter hwAdapter;
    final BulbCmdTranslator cmdTranslator;
    private volatile boolean executionFinished = false;

    //~ Construction ///////////////////////////////////////////////////////////
    public CmdHwExecutor(
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final BulbActuatorCommand command, 
            final BulbBridgeHardwareAdapter hwAdapter, 
            final BulbCmdTranslator cmdTranslator) {
        this.address = address;
        this.principal = principal;
        this.command = command;
        this.hwAdapter = hwAdapter;
        this.cmdTranslator = cmdTranslator;
    }

    public void cancelExecution() {
        this.executionFinished = true;
    }
    public boolean isExecutionFinished() {
        return executionFinished;
    }

     //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void execute() {
        BulbId bulbId = command.getBulbId();
        BulbState prevState = null;
        List<BulbState> preProcessedStates;
        while(!isExecutionFinished()){
            for (BulbState state : command.getStates()) {
                // if(log.isDebugEnabled())log.debug("Apply to bridge: " + state);
                if (state.getTransitionDelay() > 0 && prevState != null ){
                    preProcessedStates = interpolatedStateTransition(prevState, state);
                }else{
                    preProcessedStates = new ArrayList<>(1);
                    preProcessedStates.add(state);
                }

                for (BulbState state2Apply : preProcessedStates) {
                    if(state2Apply.getTransitionDelay() > 0 ){
                        try {
                            Thread.sleep(state2Apply.getTransitionDelay());
                        } catch (InterruptedException ex) {}
                    }
                    if (this.executionFinished) {
                        return;
                    }
                    try {
                        hwAdapter.applyBulbState(
                                bulbId, address, principal, state2Apply, cmdTranslator);
                        DomainEventPublisher.instance().publishDeferred(
                                new BulbStateChanged(
                                        command.getBulbId().serialize(),
                                        state2Apply) );
                    } catch (BulbBridgeHwException ex) {
                        log.warn("Error applying bulb state: ", ex);
                    } catch (Exception ex) {
                        log.error("Error during command execution: " + ex.getMessage(), ex);
                    }
                }
                prevState = state;
            }
            if(!command.isLoop() || command.getStates().size() < 2) this.executionFinished = true;
        }
    }
   
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private List<BulbState> interpolatedStateTransition(BulbState start, BulbState end){
        final List<BulbState> res;
        final Integer transitionTime = end.getTransitionDelay();
        if(transitionTime < INTERPOLATION_RESOLUTION){
            res = new ArrayList<>(1);
            res.add(end);
            return res;
        }
        
        final int countFrames = transitionTime / INTERPOLATION_RESOLUTION;
        res = new ArrayList<>(countFrames);
        res.add(start.withTransitionDelay(0));
        List<Color> colors_interpolated = start.getColor().linearInterpolation(end.getColor(), countFrames);
        BulbState tmpState;
        for (int i = 1; i < countFrames-1; i++) {
            tmpState = res.get(i-1).withColorAndTransitionDelay(
                colors_interpolated.get(i),
                INTERPOLATION_RESOLUTION );
            res.add(tmpState);
        }
        tmpState = new BulbState(
            colors_interpolated.get(countFrames - 1), 
            end.getEnabled(), 
            INTERPOLATION_RESOLUTION );
        res.add(tmpState);
        
        return res;
    }
}
