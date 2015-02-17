package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.ReqSetLightColor;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorScheme;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BulbCmdTranslator_LIFXTest {

    BulbCmdTranslator_LIFX translator = new BulbCmdTranslator_LIFX();

    final BulbBridgeId BRIDGE_ID = new BulbBridgeId("test_Bridgeuuid");
    final BulbId BULB_ID = new BulbId(BRIDGE_ID, "13");
    final BulbBridgeAddress ADDRESS = new BulbBridgeAddress("192.168.1.1", 1234, "FF:FF:FF:FF:FF:FF");
    final BulbsPrincipal PRINCIPAL = new BulbsPrincipal("test_principalNAme", AppIdCore.instance(), BRIDGE_ID.getUuId(), BulbsPrincipalState.REGISTERED);

    @Test
    public void testToApplyBulbStateCmd_ColorHSB() throws Exception {

        ColorHSB color = new ColorHSB(1f, 2f, 3f);
        BulbState state = new BulbState(color, true);
        LifxMessage<ReqSetLightColor> res = translator.toApplyBulbStateCmd(
                BULB_ID, ADDRESS, PRINCIPAL, state);

        assertThat(res.getType(), is(LifxMessageType.REQ_SET_LIGHT_COLOR));
        assertThat(res.getGatewayMacAddress().toString().toLowerCase(), is(ADDRESS.macAddress().get().toLowerCase()));
        assertThat(res.getAddress(), is(ADDRESS.toInetAddress()));
        assertThat(res.getPort(), is(ADDRESS.getPort()));

        ReqSetLightColor payload = res.getPayload();
        assertThat(payload.getFade_time().toInt(), is(3000)); // hard coded
        assertThat(payload.getKelvin().toInt(), is(0));

        assertThat(payload.getHue(), is(BT.scale(color.getHue(), 360f)));
        assertThat(payload.getSaturation(), is(BT.scale(color.getSaturation(), 255f)));
        assertThat(payload.getBrightness(), is(BT.scale(color.getBrightness(), 255f)));
    }
    @Test
    public void testToApplyBulbStateCmd_ColorRGB() throws Exception {

        ColorRGB color = new ColorRGB(10, 20, 40);
        ColorHSB color2Verify = ColorScheme.RGBtoHSB(color);
        BulbState state = new BulbState(color, true);
        LifxMessage<ReqSetLightColor> res = translator.toApplyBulbStateCmd(
                BULB_ID, ADDRESS, PRINCIPAL, state);

        assertThat(res.getType(), is(LifxMessageType.REQ_SET_LIGHT_COLOR));
        assertThat(res.getGatewayMacAddress().toString().toLowerCase(), is(ADDRESS.macAddress().get().toLowerCase()));
        assertThat(res.getAddress(), is(ADDRESS.toInetAddress()));
        assertThat(res.getPort(), is(ADDRESS.getPort()));

        ReqSetLightColor payload = res.getPayload();
        assertThat(payload.getFade_time().toInt(), is(3000)); // hard coded
        assertThat(payload.getKelvin().toInt(), is(0));

        assertThat(payload.getHue(), is(BT.scale(color2Verify.getHue(), 360f)));
        assertThat(payload.getSaturation(), is(BT.scale(color2Verify.getSaturation(), 255f)));
        assertThat(payload.getBrightness(), is(BT.scale(color2Verify.getBrightness(), 255f)));
    }

}