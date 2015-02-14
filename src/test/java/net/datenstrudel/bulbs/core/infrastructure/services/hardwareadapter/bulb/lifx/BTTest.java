package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BTTest {

    @Test
    public void scaleUint16_int() throws Exception {
        BT.Uint16 in = BT.Uint16.fromInt(0);
        assertThat(BT.scale(in, 255), is(0));

        in = BT.Uint16.fromInt(0xFFFF);
        assertThat(BT.scale(in, 255), is(255));

        in = BT.Uint16.fromBytes(new byte[]{127,127});
        assertThat(BT.scale(in, 1000), is(500));
    }
    @Test
    public void scaleUint16_float() throws Exception {
        BT.Uint16 in = BT.Uint16.fromInt(0);
        assertThat(BT.scale(in, 255f), is(0f));

        in = BT.Uint16.fromInt(0xFFFF);
        assertThat(BT.scale(in, 255f), is(255f));

        in = BT.Uint16.fromInt(  BT.Uint16.MAX.toInt()/2  );
        assertThat(BT.scale(in, 1000f), is(500f));
    }

    @Test
    public void scalefloat_Uint16() throws Exception {
        float in = 0f;
        assertThat(BT.scale(in, 255f), is(BT.Uint16.fromInt(0)) );

        in = 255f;
        assertThat(BT.scale(in, 255f), is(BT.Uint16.fromInt(0xFFFF)));

        in = 500f;
        assertThat(BT.scale(in, 1000f), is(BT.Uint16.fromInt(  BT.Uint16.MAX.toInt()/2 )) );
    }

}