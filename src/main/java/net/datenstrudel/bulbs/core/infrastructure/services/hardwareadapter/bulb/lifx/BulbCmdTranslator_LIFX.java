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
public class BulbCmdTranslator_LIFX implements BulbCmdTranslator<DatagramPacket, DatagramPacket> {

    @Override
    public BulbBridge bridgeFromJson(
            DatagramPacket payload,
            BulbBridgeId bridgeId,
            BulbBridgeAddress localAddress,
            BulbsContextUserId contextUserId) {
        return null;
    }
    @Override
    public BulbId[] bulbIdsFromJson(
            DatagramPacket payload,
            BulbBridgeId bridgeId) {
        return new BulbId[0];
    }
    @Override
    public Bulb bulbFromJson(
            DatagramPacket payload,
            BulbBridge parentBridge,
            BulbId bulbId) {
        return null;
    }
    @Override
    public BulbState stateFromJson(DatagramPacket payload) {
        return null;
    }
    @Override
    public Set<BulbsPrincipal> bulbsPrincipalsFromJson(DatagramPacket payload,BulbBridgeId bridgeId) {
        return null;
    }

    @Override
    public HwResponse responseFromJson(DatagramPacket payload, HttpStatus httpStatuscode) {
        return null;
    }
    @Override
    public InvocationResponse responseFromHardwareInvocation(DatagramPacket payload) {
        return null;
    }
    @Override
    public InvocationResponse checkResponseForError(DatagramPacket payload) {
        return null;
    }
    @Override
    public DatagramPacket toBridgeFromHwInterfaceCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toToBulbsPrincipalsCmd(BulbBridge bridge, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toCreateBulbsPrincipalCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toRemoveBulbsPrincipalCmd(BulbBridgeAddress address, BulbsPrincipal principal, BulbsPrincipal principal2Remove) {
        return null;
    }
    @Override
    public DatagramPacket toDiscoverNewBulbsCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toModifyBridgeAttributesCmd(BulbBridgeAddress address, BulbsPrincipal principal, Map<String, Object> attributes) {
        return null;
    }
    @Override
    public DatagramPacket toBulbsFromHwInterfaceCmd(BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toBulbFromHwInterfaceCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal) {
        return null;
    }
    @Override
    public DatagramPacket toModifyBulbAttributesCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal, Map<String, Object> attributes) {
        return null;
    }
    @Override
    public DatagramPacket toApplyBulbStateCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal, BulbState state) {
        return null;
    }
}
