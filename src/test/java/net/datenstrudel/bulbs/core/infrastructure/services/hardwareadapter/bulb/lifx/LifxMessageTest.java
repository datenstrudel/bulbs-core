package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.RespGetPanGateway;
import org.junit.Test;
import sun.invoke.empty.Empty;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LifxMessageTest {

    @Test
    public void toBytesFromBytesConsistent() throws Exception {

        LifxMessage<LifxMessagePayload.EmptyPayload> testInst =
                new LifxMessage<>(LifxPacketType.REQ_PAN_GATEWAY, LifxMessagePayload.emptyPayload());

        byte[] bytes = testInst.toBytes();

        LifxMessage lifxMessage = LifxMessage.fromBytes(bytes);
        assertThat(lifxMessage, is(testInst));

    }
}