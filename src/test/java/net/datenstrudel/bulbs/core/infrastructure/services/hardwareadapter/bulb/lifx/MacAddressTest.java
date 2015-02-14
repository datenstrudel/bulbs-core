package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MacAddressTest {

    @Test
    public void testToString() throws Exception {
        byte[] origMax = new byte[]{-48, 115, -43, 1, -78, 17};
        MacAddress addr = MacAddress.fromBytes(origMax);
        assertThat(MacAddress.fromString(addr.toString()).getBytes(), is(origMax));
    }
}