package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;

import java.util.Collection;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 */
public interface ActuatorService {

    /**
     * Executes the <code>command</code> given in a deferred way by putting it on a local
     * queue which is processed in a separately timed thread. The separate thread is executed
     * within a fixed interval in order to read from that queue and execute the commands.
     * Commands are stored, associated by a significant hash code, that is derived from
     * information about the executing user and the targeted resource, as well as the
     * application from which the command originated. Multiple commands, having the same hash,
     * that occur during the sleep cycle of the separate thread will be discarded and result
     * in execution of the last command that occurred during the cycle. <br />
     * This way the load on this application and on the underlying hardware is kept within
     * processable bounds, which allows a client to call actuations as often as desired
     * without overloading. This is especially required for use cases where a continiuous stream
     * of commands is hitting this service, like adjusting brightnes with a slider control.
     *
     * @param command
     * @throws BulbBridgeHwException
     */
    void executeDeferred(AbstractActuatorCmd command) throws BulbBridgeHwException;

    void execute(AbstractActuatorCmd command) throws BulbBridgeHwException;

    void execute(Collection<? extends AbstractActuatorCmd> commands) throws BulbBridgeHwException;
}