package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BulbLabelPayloadTest {

    @Test
    public void newSetBulblabelPayload() throws Exception {
        BulbLabelPayload res = BulbLabelPayload.newSetBulblabelPayload("1234567890123AZ6789012I456789012");
        assertThat(res.getBulbLabel().getData().length, is(32));
        assertThat(res.getBulbLabel().toString(), is("1234567890123AZ6789012I456789012"));
        assertThat(res.getPacketType(), is(LifxPacketType.REQ_SET_BULB_LABEL));
    }

    @Test
    public void fromRawData() throws Exception {
        BulbLabelPayload res = BulbLabelPayload.fromRawData(BT.CharArray.fromString("Test_Label").getData());
        assertThat(res.getBulbLabelAsString(), is("Test_Label"));
        assertThat(res.getPacketType(), is(LifxPacketType.RESP_BULB_LABEL));
    }
}