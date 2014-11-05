package net.datenstrudel.bulbs.core.infrastructure.services.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;

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
            BulbCmdTranslator cmdTranslator)throws BulbBridgeHwException;;
            
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    
    //~ BULB /////////////////////////////////////////////////////////////////////
    public BulbId[] toBulbIds(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    public Bulb[] toBulbs(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator)throws BulbBridgeHwException;
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator)throws BulbBridgeHwException;
    
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address, 
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
    
    public InvocationResponse applyBulbState(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbState state, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException;
}