package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LifxMessageTest {

    @Test
    public void toBytesFromBytesConsistent() throws Exception {

        LifxMessage<LifxMessagePayload.EmptyPayload> testInst =
                LifxMessage.messageFrom(LifxMessagePayload.emptyPayload(LifxPacketType.REQ_PAN_GATEWAY));

        byte[] bytes = testInst.toBytes();

        LifxMessage lifxMessage = LifxMessage.fromBytes(bytes, null, 0);
        assertThat(lifxMessage, is(testInst));

    }
}