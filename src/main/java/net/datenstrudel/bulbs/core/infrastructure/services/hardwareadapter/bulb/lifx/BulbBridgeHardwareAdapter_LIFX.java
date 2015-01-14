package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;

import java.util.Map;
import java.util.Set;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbBridgeHardwareAdapter_LIFX implements BulbBridgeHardwareAdapter<BulbCmdTranslator_LIFX> {

    @Override
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }
    @Override
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }
    @Override
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public BulbId[] toBulbIds(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return new BulbId[0];
    }

    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return new Bulb[0];
    }

    @Override
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse applyBulbState(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbCmdTranslator_LIFX cmdTranslator) throws BulbBridgeHwException {
        return null;
    }
}
