package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.PowerStatePayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.ReqSetLightColor;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Thomas Wendzinski.
 */

@ContextConfiguration(
        initializers = TestConfig.class,
        classes = {
                BulbsCoreConfig.class,
                TestConfig.class
        })
@RunWith(SpringJUnit4ClassRunner.class)
public class BulbBridgeHardwareAdapter_LifxIT {

    @Autowired
    BulbBridgeHardwareAdapter_LIFX hwAdapter;

    UdpLifxMessageTransportManager mk_transport;

    @Before
    public void before() {
        mk_transport = Mockito.mock(UdpLifxMessageTransportManager.class);
        ReflectionTestUtils.setField(hwAdapter, "transportManager", mk_transport);
    }

    @Test
    public void applyBulbState_noColorNoPowerStateDiffNoMessageSent() throws Exception {
        BulbBridgeId bridgeID = new BulbBridgeId("test_bridgeUuid");
        BulbBridgeAddress address = new BulbBridgeAddress("192.168.1.4", -1);
        BulbId bulbId = new BulbId(bridgeID, 14);
        BulbsPrincipal principal = new BulbsPrincipal("test_User", AppIdCore.instance(), bridgeID.getUuId(), BulbsPrincipalState.REGISTERED);
        hwAdapter.applyBulbState(
                bulbId, address, principal, new BulbState(true), BulbsPlatform.LIFX, new BulbState(true));
        verify(mk_transport, never()).send( any(LifxMessage.class));

    }
    @Test
    public void applyBulbState_prevStateNullPowerStateSetOnly() throws Exception {
        BulbBridgeId bridgeID = new BulbBridgeId("test_bridgeUuid");
        BulbBridgeAddress address = new BulbBridgeAddress("192.168.1.4", -1, "11:22:33:44:55:66");
        BulbId bulbId = new BulbId(bridgeID, 14);
        BulbsPrincipal principal = new BulbsPrincipal("test_User", AppIdCore.instance(), bridgeID.getUuId(), BulbsPrincipalState.REGISTERED);
        doAnswer( inv -> {
            assertThat(inv.getArguments()[0], is(notNullValue()));
            LifxMessage<PowerStatePayload> msg = (LifxMessage)inv.getArguments()[0];
            assertThat(msg.getGatewayMacAddress().toString(), is(address.macAddress().get()));
            assertThat(msg.getAddress(), is(address.toInetAddress()));
            assertThat(msg.getPayload().getState(), is(PowerStatePayload.Power.ON));
            return null;
        }).when(mk_transport).send(any(LifxMessage.class));
        hwAdapter.applyBulbState(
                bulbId, address, principal, new BulbState(true), BulbsPlatform.LIFX, null);
        verify(mk_transport, atLeastOnce()).send(any(LifxMessage.class));
    }
    @Test
    public void applyBulbState_prevStateDiffsNewState_PowerStateSetOnly() throws Exception {
        BulbBridgeId bridgeID = new BulbBridgeId("test_bridgeUuid");
        BulbBridgeAddress address = new BulbBridgeAddress("192.168.1.4", -1, "11:22:33:44:55:66");
        BulbId bulbId = new BulbId(bridgeID, 14);
        BulbsPrincipal principal = new BulbsPrincipal("test_User", AppIdCore.instance(), bridgeID.getUuId(), BulbsPrincipalState.REGISTERED);
        doAnswer( inv -> {
            assertThat(inv.getArguments()[0], is(notNullValue()));
            LifxMessage<PowerStatePayload> msg = (LifxMessage)inv.getArguments()[0];
            assertThat(msg.getGatewayMacAddress().toString(), is(address.macAddress().get()));
            assertThat(msg.getAddress(), is(address.toInetAddress()));
            assertThat(msg.getPayload().getState(), is(PowerStatePayload.Power.ON));
            return null;
        }).when(mk_transport).send(any(LifxMessage.class));
        hwAdapter.applyBulbState(
                bulbId, address, principal, new BulbState(true), BulbsPlatform.LIFX, new BulbState(false) );
        verify(mk_transport, atLeastOnce()).send(any(LifxMessage.class));
    }
    @Test
    public void applyBulbState() throws Exception {
        BulbBridgeId bridgeID = new BulbBridgeId("test_bridgeUuid");
        BulbBridgeAddress address = new BulbBridgeAddress("192.168.1.4", -1, "11:22:33:44:55:66");
        BulbId bulbId = new BulbId(bridgeID, 14);
        BulbsPrincipal principal = new BulbsPrincipal("test_User", AppIdCore.instance(), bridgeID.getUuId(), BulbsPrincipalState.REGISTERED);
        doAnswer( inv -> {
            assertThat(inv.getArguments()[0], is(notNullValue()));
            LifxMessage<ReqSetLightColor> msg = (LifxMessage)inv.getArguments()[0];
            assertThat(msg.getGatewayMacAddress().toString(), is(address.macAddress().get()));
            assertThat(msg.getAddress(), is(address.toInetAddress()));
            return null;
        }).when(mk_transport).send(any(LifxMessage.class));

        hwAdapter.applyBulbState(
                bulbId, address, principal, new BulbState(new ColorHSB(1f,2f,3f), true), BulbsPlatform.LIFX, null );
        verify(mk_transport, atLeastOnce()).send( any(LifxMessage.class));
    }
}
