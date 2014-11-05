package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;

import java.util.Map;
import java.util.Set;

/**
 * This service represents a hardware independent adapter for domain objects in order
 * to communicate with underlying hardware. <br />
 * It allows multi threaded bulb actuation that may requires some more
 * time to execute which should not cause blocking on the invoking client domain object.<br />
 * At certain points of execution it is assumed that underlying hardware has received
 * the ocmmand and is going to apply it shortly. At these points of execution events 
 * are fired in order to keep the underlying domain model (eventually) consistent.
 * 
 * @author Thomas Wendzinski
 * @version 1.0
 * @updated 08-Jun-2013 22:54:57
 */
public interface BulbsHwService {

    //~ BRIDGE /////////////////////////////////////////////////////////////////
    /**
     * @param address
     * @param bridgeId
     * @param principal
     * @param contextUserId 
     * @param platform 
     * @return 
     * @throws BulbBridgeHwException 
     */
    public BulbBridge bridgeFromHwInterface(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException;

    public void discoverNewBulbs(
            BulbBridgeId bridgeId,
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public Set<BulbsPrincipal> bulbsPrincipalsFromHwInterface(
            BulbBridge bridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    /**
     * Creates a new principal (used for hardware dependend authentication and authorization).
     * Usually Link Button must have been pressed.
     * @param address
     * @param principal
     * @param platform
     * @return
     * @throws BulbBridgeHwException 
     */
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    //~ BULBs /////////////////////////////////////////////////////////////////
    public BulbId[] bulbIdsFromHwInterface(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    public Bulb[] bulbsFromHwInterface(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public Bulb bulbFromHwInterface(
            BulbId bulbId,
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address, 
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    /**
     * Cancel a (lengthy) actuation 
     * @param id 
     */
    public void cancelActuation(BulbId id);
    
    public void executeBulbActuation(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbActuatorCommand command,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
}