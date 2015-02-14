package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;

import java.nio.ByteBuffer;

/**
 * Created by Thomas Wendzinski.
 */
public class ReqSetLightColor extends LifxMessagePayload {
    final byte stream         = 0;        // Unknown, potential "streaming" mode toggle? Set to 0x00 for now.
    final BT.Uint16 hue;                 // LE NOTE: Wraps around at 0xff 0xff back to 0x00 0x00 which is a primary red colour.
    final BT.Uint16 saturation;             // LE
    final BT.Uint16 brightness;  // LE
    final BT.Uint16 kelvin;      // LE i.e. colour temperature (whites wheel in apps)
    final BT.Uint32 fade_time;   // LE Length of fade action, in seconds

    /**
     * HSB constructor
     * @param brightness
     * @param hue
     * @param saturation
     * @param fade_time
     */
    public ReqSetLightColor(
            BT.Uint16 hue,
            BT.Uint16 saturation,
            BT.Uint16 brightness,
            BT.Uint32 fade_time) {
        super(LifxPacketType.REQ_SET_LIGHT_COLOR);
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.kelvin = BT.Uint16.fromInt(0);
        this.fade_time = fade_time;
    }
    public ReqSetLightColor(
            BT.Uint16 kelvin,
            BT.Uint32 fade_time) {
        super(LifxPacketType.REQ_SET_LIGHT_COLOR);
        this.kelvin = kelvin;
        this.fade_time = fade_time;

        this.brightness = BT.Uint16.fromInt(0);
        this.hue = BT.Uint16.fromInt(0);
        this.saturation = BT.Uint16.fromInt(0);
    }

    @Override
    public byte[] process2Bytes() {
        if(data != null) return data;
        final ByteBuffer res = ByteBuffer.allocate(13);
        res.put(stream);
        res.put(hue.getData_LE());
        res.put(saturation.getData_LE());
        res.put(brightness.getData_LE());
        res.put(kelvin.getData_LE());
        res.put(fade_time.getData_LE());
        return res.array();
    }

    public BT.Uint16 getBrightness() {
        return brightness;
    }
    public BT.Uint32 getFade_time() {
        return fade_time;
    }
    public BT.Uint16 getHue() {
        return hue;
    }
    public BT.Uint16 getKelvin() {
        return kelvin;
    }
    public BT.Uint16 getSaturation() {
        return saturation;
    }
}
