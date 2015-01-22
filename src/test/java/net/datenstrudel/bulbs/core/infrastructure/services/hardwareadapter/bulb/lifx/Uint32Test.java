package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class Uint32Test {

    @Test
    public void fromInt_ToInt() throws Exception {
        BT.Uint32 res = BT.Uint32.fromInt(Integer.MAX_VALUE - 2);
        assertThat(res.toInt(), is(Integer.MAX_VALUE - 2) );
    }

}