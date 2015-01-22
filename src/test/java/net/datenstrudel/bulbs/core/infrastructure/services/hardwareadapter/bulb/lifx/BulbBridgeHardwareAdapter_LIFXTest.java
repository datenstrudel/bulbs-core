package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.DatagramPacket;
import java.net.Inet4Address;

public class BulbBridgeHardwareAdapter_LIFXTest {

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFXTest.class);
    BulbBridgeHardwareAdapter_LIFX hwAdapter = new BulbBridgeHardwareAdapter_LIFX();

    @Test
    public void sendDatagramMessage() throws Exception {
        ReflectionTestUtils.invokeMethod(hwAdapter, "provideNewSocket", null);
//        while (true) {
            hwAdapter.sendDatagramMessage(
                    new LifxMessage(LifxPacketType.REQ_PAN_GATEWAY,
                            LifxMessagePayload.emptyPayload()),
                    Inet4Address.getByName("192.168.1.255"), 56700);
            DatagramPacket datagramPacket = hwAdapter.retrieveDatagramMessage();
            LifxMessage res = LifxMessage.fromBytes(datagramPacket.getData());
//            res = hwAdapter.retrieveDatagramMessage();
//            res = hwAdapter.retrieveDatagramMessage();
            log.info("" + res);
            res.toStringConsole();

            Thread.sleep(3500);


    }

    @Test
    public void testRetrieveDatagramMessage() throws Exception {

    }
}