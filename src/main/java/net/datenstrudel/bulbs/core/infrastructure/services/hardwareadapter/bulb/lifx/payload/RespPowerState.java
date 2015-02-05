package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Thomas Wendzinski.
 */
public class RespPowerState extends LifxMessagePayload {

    private Power state;

    private RespPowerState() {
    }

    public RespPowerState(Power state) {
        this.state = state;
        setData(this.state.value.getData());
    }
    public RespPowerState(byte[] data) {
        super(data);
        decodeFromRawdata();
    }

    private void decodeFromRawdata() {
        this.state = Power.fromByteValue(Arrays.copyOf(data, 4));
    }

    public static enum Power{
        OFF  ( BT.Uint32.fromInt(0x0000)),
        ON   ( BT.Uint32.fromInt(0xFFFF)),
        ;
        private BT.Uint32 value;

        private Power(BT.Uint32 value) {
            this.value = value;
        }
        public static Power fromByteValue(byte[] value) {
            BT.Uint32 search = BT.Uint32.fromBytes(value);
            Optional<Power> res = Arrays.stream(Power.values()).filter(s -> s.value.equals(search)).findFirst();
            if (!res.isPresent()) throw new IllegalArgumentException("Service not found for given value: " + value);
            return res.get();
        }
    }

    @Override
    public String toString() {
        return "RespGetPanGateway{" +
                "power=" + state+
                '}';
    }
}
