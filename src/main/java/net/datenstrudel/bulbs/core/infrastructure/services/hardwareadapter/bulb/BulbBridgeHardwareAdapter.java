package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;

import java.util.Map;
import java.util.Set;

/**
 * Represents the hardware interface of a <code>BulbBridge</code> that is used in order to
 * communicate with the actual underlying hardware.
 * @author Thomas Wendzinski
 * @version 1.0
 * @updated 08-Jun-2013 22:54:56
 */
public interface BulbBridgeHardwareAdapter {

    //~ BRIDGE ///////////////////////////////////////////////////////////////////
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform)throws BulbBridgeHwException;
            
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException;
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException;
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    //~ BULB /////////////////////////////////////////////////////////////////////
    public BulbId[] toBulbIds(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException;
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform)throws BulbBridgeHwException;
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform)throws BulbBridgeHwException;
    
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException;
    
    public void applyBulbState(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbsPlatform platform) throws BulbBridgeHwException;
}