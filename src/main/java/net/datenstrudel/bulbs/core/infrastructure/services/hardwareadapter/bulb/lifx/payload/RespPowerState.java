package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Thomas Wendzinski.
 */
public class RespPowerState extends LifxMessagePayload {

    private Power state;

    private RespPowerState() {
        super(LifxPacketType.RESP_POWER_STATE);
    }

    public RespPowerState(Power state) {
        super(LifxPacketType.RESP_POWER_STATE);
        this.state = state;
        setData(this.state.value.getData());
    }
    public RespPowerState(byte[] data) {
        super(LifxPacketType.RESP_POWER_STATE, data);
        decodeFromRawdata();
    }

    private void decodeFromRawdata() {
        this.state = Power.fromByteValue(Arrays.copyOf(data, 2));
    }

    public static enum Power{
        OFF  ( BT.Uint16.fromInt(0x0000)),
        ON   ( BT.Uint16.fromInt(0xFFFF)),
        ;
        private BT.Uint16 value;

        private Power(BT.Uint16 value) {
            this.value = value;
        }
        public static Power fromByteValue(byte[] value) {
            BT.Uint16 search = BT.Uint16.fromBytes(value);
            Optional<Power> res = Arrays.stream(Power.values()).filter(s -> s.value.equals(search)).findFirst();
            if (!res.isPresent()) throw new IllegalArgumentException("Service not found for given value: " + value);
            return res.get();
        }
    }

    @Override
    protected byte[] process2Bytes() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RespPowerState that = (RespPowerState) o;

        if (state != that.state) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "RespGetPanGateway{" +
                "power=" + state+
                '}';
    }
}
