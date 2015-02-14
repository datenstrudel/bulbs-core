package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbCmdTranslator;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.BulbLabelPayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.ReqSetLightColor;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.RespLightState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorScheme;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbCmdTranslator_LIFX implements BulbCmdTranslator<LifxMessage, LifxMessage> {

    @Override
    public BulbBridge bridgeFromPayload(
            LifxMessage payload,
            BulbBridgeId bridgeId,
            BulbBridgeAddress localAddress,
            BulbsContextUserId contextUserId) {
//        RespGetPanGateway resp = (RespGetPanGateway) payload.getPayload();
        BulbBridge res = new BulbBridge(
                bridgeId,
                payload.getMacAddress().toString(),
                BulbsPlatform.LIFX,
                new BulbBridgeAddress(payload.getAddress().getHostAddress(), payload.getPort(), payload.getMacAddress().toString()),
                "LIFX_BRIDGE",
                contextUserId,
                new HashMap<>()
        );
        return res;
    }
    @Override
    public BulbId[] bulbIdsFromPayload(
            LifxMessage payload,
            BulbBridgeId bridgeId) {
        return new BulbId[0];
    }
    @Override
    public Bulb bulbFromPayload(
            LifxMessage payload,
            BulbBridge parentBridge,
            BulbId bulbId) {

        RespLightState resp = (RespLightState) payload.getPayload();
        Bulb res = new Bulb(bulbId, BulbsPlatform.LIFX,
                new String(resp.getBulbLabel().toString()), parentBridge,
                stateFrom(resp), false, new HashMap<>());
        return res;
    }

    private BulbState stateFrom(RespLightState in) {
        BulbState res = new BulbState(new ColorHSB(
                BT.scale(in.getHue(), 360f),
                BT.scale(in.getSaturation(), 255f),
                BT.scale(in.getBrightness(), 255f) ),
                in.getPower().toInt() != 0
        );
        return res;
    }

    @Override
    public BulbState stateFromPayload(LifxMessage payload) {

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
        String name = (String) attributes.get("name");
        if(name == null)
            throw new UnsupportedOperationException("Attribute(s) supplied not supported to be changed by lifx" + attributes.keySet());
        return LifxMessage.messageFrom(
                BulbLabelPayload.newSetBulblabelPayload(name),
                address.toInetAddress(), address.getPort(), MacAddress.fromString(address.macAddress().get()));
    }
    @Override
    public LifxMessage toApplyBulbStateCmd(BulbId bulbId, BulbBridgeAddress address, BulbsPrincipal principal, BulbState state) {
        ColorHSB color;
        switch ( state.getColor().colorScheme() ){
            case HSB:
                color = (ColorHSB) state.getColor();
                break;
            case RGB:
                color = ColorScheme.RGBtoHSB((ColorRGB)state.getColor());
                break;
            case TEMP:
            default:
                throw new UnsupportedOperationException(
                        "ColorScheme[" + state.getColor().colorScheme()
                                + "] not supported by platform[" + BulbsPlatform.LIFX + "]");
        }
        return LifxMessage.messageFrom(
                new ReqSetLightColor(
                        BT.scale(color.getHue(), 360f),
                        BT.scale(color.getSaturation(), 255f),
                        BT.scale(color.getBrightness(), 255f),
                        BT.Uint32.fromInt(3000) // Fixme What is it? Could be ms/10
                ),
                address.toInetAddress(), address.getPort(), MacAddress.fromString(address.macAddress().get()));
    }

}
