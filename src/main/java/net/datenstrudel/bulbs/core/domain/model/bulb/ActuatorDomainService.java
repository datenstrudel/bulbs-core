package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;

import java.util.Collection;

/**
 * Service wrapping knowledge of how to execute specific {@link AbstractActuatorCmd}s.
 * @author Thomas Wendzinski
 */
public interface ActuatorDomainService {
    
    /**
     * Puts the given command to a queue, with a command type specific hash. During a fixed
     * duty cycle the last command receiceived is beeing executed. This is useful when a 
     * huge amount of commands is sent to the application but the actual number of commands
     * sent to the underlying hardware shall be limited.
     * @param command 
     */
    public void executeDeferred(AbstractActuatorCmd command);
    /**
     * Immediately executes the given <code>command</code>.
     * @param command 
     * @throws net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException
     */
    public void execute(AbstractActuatorCmd command)throws BulbBridgeHwException;
    public void execute(Collection<? extends AbstractActuatorCmd> command)throws BulbBridgeHwException;
    
    //~ Command Specific /////////////////////////////////////////////////////
    public void execute(BulbActuatorCommand bulbCommand)throws BulbBridgeHwException;
    public void execute(GroupActuatorCommand groupCommand)throws BulbBridgeHwException;
    public void execute(PresetActuatorCommand presetCommand)throws BulbBridgeHwException;
    public void execute(ActuationCancelCommand cancelCommand)throws BulbBridgeHwException;

}
