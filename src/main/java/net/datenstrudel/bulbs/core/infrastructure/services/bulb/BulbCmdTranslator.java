package net.datenstrudel.bulbs.core.infrastructure.services.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

/**
 * This interface separates the translation of <b>hardware vendor specific commands<(b> and
 * <b>command responses</b> from technical implementation of communication mechanisms.
 * 
 * @author Thomas Wendzinski
 */
public interface BulbCmdTranslator {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbBridge bridgeFromJson(String json, BulbBridgeId bridgeId, BulbBridgeAddress localAddress,
            BulbsContextUserId contextUserId);
    public BulbId[] bulbIdsFromJson(String json, BulbBridgeId bridgeId);
    public Bulb bulbFromJson(String json, BulbBridge parentBridge, BulbId bulbId);
    public BulbState stateFromJson(String json);
    public Set<BulbsPrincipal> bulbsPrincipalsFromJson(String json, BulbBridgeId bridgeId);
    public HwResponse responseFromJson(String json, HttpStatus httpStatuscode);
    public InvocationResponse responseFromHardwareInvocation(String json);
    /**
     * Check the given response as <code>json</code> whether it represents an error and return
     * it as <code>InvocationResponse</code>. Return <code>null</code> if there was no error.
     * 
     * @param json
     * @return Error response or <code>null</code> if there was no error.
     */
    public InvocationResponse checkResponseForError(String json);
    
    ////////////////////////////////////////////////////////////////////////////
    public HttpCommand toBridgeFromHwInterfaceCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal );
    public HttpCommand toToBulbsPrincipalsCmd(
            BulbBridge bridge,
            BulbsPrincipal principal );
    public HttpCommand toCreateBulbsPrincipalCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal );
    public HttpCommand toRemoveBulbsPrincipalCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove );
    public HttpCommand toDiscoverNewBulbsCmd(
            BulbBridgeAddress address, 
            BulbsPrincipal principal);
    public HttpCommand toModifyBridgeAttributesCmd(
            BulbBridgeAddress address, 
            BulbsPrincipal principal,
            Map<String, Object> attributes);
    public HttpCommand toBulbsFromHwInterfaceCmd(
            BulbBridgeAddress address, 
            BulbsPrincipal principal);
    public HttpCommand toBulbFromHwInterfaceCmd(
            BulbId bulbId,
            BulbBridgeAddress address, 
            BulbsPrincipal principal);
    
    public HttpCommand toModifyBulbAttributesCmd(
            BulbId bulbId,
            BulbBridgeAddress address, 
            BulbsPrincipal principal,
            Map<String, Object> attributes);
    public HttpCommand toApplyBulbStateCmd(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal,
            BulbState state);
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
