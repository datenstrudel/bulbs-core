package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class Uint16Test {

    @Test
    public void fromInt() throws Exception {
        BT.Uint16 res = BT.Uint16.fromInt(257);
        assertThat(res.getData(), is(new byte[]{1, 1}) );
    }
    @Test
    public void fromInt_Truncated() throws Exception {
        BT.Uint16 res = BT.Uint16.fromInt(ByteBuffer.wrap(new byte[]{0,1,1,1}).getInt());
        assertThat(res.getData(), is(new byte[]{1, 1}) );
    }
    @Test
    public void from_LE() throws Exception {
        BT.Uint16 res = BT.Uint16.fromBytes_LE(new byte[]{1, 0});
        assertThat(res.getData(), is(new byte[]{0, 1}) );
    }
    @Test(expected = IllegalArgumentException.class)
    public void from_LE__TooShort() throws Exception {
        BT.Uint16.fromBytes_LE(new byte[]{1});
    }
    @Test
    public void from_LE__TooLarge() throws Exception {
        BT.Uint16 res = BT.Uint16.fromBytes_LE(new byte[]{1, 0, 2, 3});
        assertThat(res.getData(), is(new byte[]{0, 1}) );
    }
    @Test
     public void getData_LE() throws Exception {
        BT.Uint16 res = BT.Uint16.fromInt(1);
        assertThat(res.getData_LE(), is(new byte[]{1, 0}));
    }

    @Test
    public void toInt() throws Exception {
        BT.Uint16 res = BT.Uint16.fromInt(1566);
        assertThat(res.toInt(), is(1566));
    }

}