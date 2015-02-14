package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MacAddressTest {

    @Test
    public void testToString() throws Exception {
        byte[] origMac = new byte[]{-48, 115, -43, 1, -78, 17};
        MacAddress addr = MacAddress.fromBytes(origMac);
        assertThat(MacAddress.fromString(addr.toString()).getBytes(), is(origMac));
    }

    @Test
    public void fromString() throws Exception{
        String origMac = "11:22:33:44:55:66";
        MacAddress addr = MacAddress.fromString(origMac);
        assertThat(addr.toString(), is(origMac));
    }
}