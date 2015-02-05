package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.RespPowerState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;

public class BulbBridgeHardwareAdapter_LIFXTest {

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFXTest.class);
//    BulbBridgeHardwareAdapter_LIFX hwAdapter = new BulbBridgeHardwareAdapter_LIFX();

    @Test
    public void playAndTry() throws Exception {
//        ReflectionTestUtils.invokeMethod(hwAdapter, "provideNewUdpSocket", BulbBridgeHardwareAdapter_LIFX.LIFX_STD_PORT);
////        while (true) {
//        hwAdapter.sendDatagramMessage(new LifxMessage<>(
//                        LifxPacketType.REQ_PAN_GATEWAY,
//                        LifxMessagePayload.emptyPayload(),
//                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
//              )
//        );
//        DatagramPacket datagramPacket = hwAdapter.retrieveDatagramMessage();
//        LifxMessage discoveryRes = LifxMessage.fromBytes(datagramPacket.getData());
//        log.info("" + discoveryRes);
//        discoveryRes.toStringConsole();
//
//        hwAdapter.sendDatagramMessage(
//                new LifxMessage<>(
//                        LifxPacketType.REQ_POWER_STATE,
//                        LifxMessagePayload.emptyPayload(),
//                        InetAddress.getByName("192.168.1.5"),
//                        56700, discoveryRes.getTarget_mac_address())
//        );
//        DatagramPacket packet_Power = hwAdapter.retrieveDatagramMessage();
//
//        LifxMessage<RespPowerState> pwrState =  LifxMessage.fromBytes(packet_Power.getData());
//        RespPowerState pwrStatePld = pwrState.getPayload();

        Thread.sleep(3500);


    }

    @Test
    public void testRetrieveDatagramMessage() throws Exception {

    }

//    byte[] pwrStateIn = hwAdapter.sendAndReceiveTcpMessage(
//            new LifxMessage(
//                    LifxPacketType.REQ_POWER_STATE,
//                    LifxMessagePayload.emptyPayload(),
//                    InetAddress.getByName("192.168.1.5"),
//                    56700,
//                    discoveryRes.getTarget_mac_address())
//    );
}

