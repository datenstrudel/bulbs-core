package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;

import java.nio.ByteBuffer;

/**
 * Created by Thomas Wendzinski.
 */
public class RespLightState extends LifxMessagePayload {

    BT.Uint16 hue;          // LE
    BT.Uint16 saturation;   // LE
    BT.Uint16 brightness;   // LE
    BT.Uint16 kelvin;       // LE
    BT.Uint16 dim;          // LE?
    BT.Uint16 power;
    BT.CharArray bulbLabel; // UTF-8 encoded string
    BT.Uint64 tags;

    public RespLightState() {
        super(LifxPacketType.RESP_LIGHT_STATE);
    }
    public RespLightState(byte[] data) {
        super(LifxPacketType.RESP_LIGHT_STATE, data);
        decodeFromRawdata();
    }

    private void decodeFromRawdata() {
        ByteBuffer wrap = ByteBuffer.wrap(this.data);
        byte[] tmp;

        tmp = new byte[2];
        wrap.get(tmp);
        this.hue = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        wrap.get(tmp);
        this.saturation = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        wrap.get(tmp);
        this.brightness = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        wrap.get(tmp);
        this.kelvin = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        wrap.get(tmp);
        this.dim = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        wrap.get(tmp);
        this.power = BT.Uint16.fromBytes(tmp);

        tmp = new byte[32];
        wrap.get(tmp);
        this.bulbLabel = BT.CharArray.fromBytes(tmp);
    }

    public BT.Uint16 getBrightness() {
        return brightness;
    }
    public BT.CharArray getBulbLabel() {
        return bulbLabel;
    }
    public BT.Uint16 getDim() {
        return dim;
    }
    public BT.Uint16 getHue() {
        return hue;
    }
    public BT.Uint16 getKelvin() {
        return kelvin;
    }
    public BT.Uint16 getPower() {
        return power;
    }
    public BT.Uint16 getSaturation() {
        return saturation;
    }
    public BT.Uint64 getTags() {
        return tags;
    }

    @Override
    protected byte[] process2Bytes() {
        return null;
    }
}
