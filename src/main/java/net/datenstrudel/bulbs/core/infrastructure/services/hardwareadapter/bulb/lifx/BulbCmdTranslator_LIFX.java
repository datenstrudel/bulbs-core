package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbCmdTranslator;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.springframework.http.HttpStatus;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Set;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbCmdTranslator_LIFX implements BulbCmdTranslator<LifxMessage, LifxMessage> {

    @Override
    public BulbBridge bridgeFromJson(
            LifxMessage payload,
            BulbBridgeId bridgeId,
            BulbBridgeAddress localAddress,
            BulbsContextUserId contextUserId) {
        return null;
    }
    @Override
    public BulbId[] bulbIdsFromJson(
            LifxMessage payload,
            BulbBridgeId bridgeId) {
        return new BulbId[0];
    }
    @Override
    public Bulb bulbFromJson(
            LifxMessage payload,
            BulbBridge parentBridge,
            BulbId bulbId) {
        return null;
    }
    @Override
    public BulbState stateFromJson(LifxMessage payload) {
        return null;
    }
    @Override
    public Set<BulbsPrincipal> bulbsPrincipalsFromJson(LifxMessage payload, BulbBridgeId bridgeId) {
        return null;
    }

    @Override
    public HwResponse responseFromJson(LifxMessage payload, HttpStatus httpStatuscode) {
        return null;
    }
    @Override
    public InvocationResponse responseFromHardwareInvocation(LifxMessage payload) {
        return null;
    }
    @Override
    public InvocationResponse checkResponseForError(LifxMessage payload) {
        return null;
    }
    @Override
    public LifxMessage toBridgeFromHwInterfaceCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toToBulbsPrincipalsCmd(BulbBridge bridge, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toCreateBulbsPrincipalCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toRemoveBulbsPrincipalCmd(BulbBridgeAddress address, BulbsPrincipal principal, BulbsPrincipal principal2Remove) {
        return null;
    }
    @Override
    public LifxMessage toDiscoverNewBulbsCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toModifyBridgeAttributesCmd(BulbBridgeAddress address, BulbsPrincipal principal, Map<String, Object> attributes) {
        return null;
    }
    @Override
    public LifxMessage toBulbsFromHwInterfaceCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toBulbFromHwInterfaceCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public LifxMessage toModifyBulbAttributesCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal, Map<String, Object> attributes) {
        return null;
    }
    @Override
    public LifxMessage toApplyBulbStateCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal, BulbState state) {
        return null;
    }
}
