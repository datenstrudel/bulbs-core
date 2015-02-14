package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CharArrayTest {

    @Test
    public void fromBytes() throws Exception {
        byte[] bytes = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        BT.CharArray res = BT.CharArray.fromBytes(bytes);
        assertThat(res.toString(), is(""));
    }

    @Test @Ignore
    public void fromString() throws Exception {
        fail(); //FIXME implement me!
    }

    @Test @Ignore
    public void testToString() throws Exception {
        fail(); //FIXME implement me!
    }
}