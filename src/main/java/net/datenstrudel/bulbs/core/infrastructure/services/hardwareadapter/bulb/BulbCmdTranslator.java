package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

/**
 *
 * In/Out types produced to be uniformly processed by a corresponding
 * {@link net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter BulbBridgeHardwareAdapter}
 * @param <I> Input type, e.g. payload from hardware
 * @param <O> Output type, payload sent to hardware
 */
public interface BulbCmdTranslator<I,O> {

    public BulbBridge bridgeFromPayload(I payload, BulbBridgeId bridgeId, BulbBridgeAddress localAddress,
                                        BulbsContextUserId contextUserId);
    public BulbId[] bulbIdsFromPayload(I payload, BulbBridgeId bridgeId);
    public Bulb bulbFromPayload(I payload, BulbBridge parentBridge, BulbId bulbId);
    public BulbState stateFromPayload(I payload);
    public Set<BulbsPrincipal> bulbsPrincipalsFromJson(I payload, BulbBridgeId bridgeId);
    public HwResponse responseFromJson(I payload, HttpStatus httpStatuscode);
    public InvocationResponse responseFromHardwareInvocation(I payload);
    /**
     * Check the given response as <code>payload</code> whether it represents an error and return
     * it as <code>InvocationResponse</code>. Return <code>null</code> if there was no error.
     *
     * @param payload
     * @return Error response or <code>null</code> if there was no error.
     */
    public InvocationResponse checkResponseForError(I payload);

    ////////////////////////////////////////////////////////////////////////////
    public O toBridgeFromHwInterfaceCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal );
    public O toToBulbsPrincipalsCmd(
            BulbBridge bridge,
            BulbsPrincipal principal );
    public O toCreateBulbsPrincipalCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal );
    public O toRemoveBulbsPrincipalCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove );
    public O toDiscoverNewBulbsCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal);
    public O toModifyBridgeAttributesCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes);
    public O toBulbsFromHwInterfaceCmd(
            BulbBridgeAddress address,
            BulbsPrincipal principal);
    public O toBulbFromHwInterfaceCmd(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal);

    public O toModifyBulbAttributesCmd(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes);
    public O toApplyBulbStateCmd(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state);
}
