package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxMessageType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Thomas Wendzinski.
 */
public class PowerStatePayload extends LifxMessagePayload {

    private Power state;

    private PowerStatePayload() {
        super(LifxMessageType.RESP_POWER_STATE);
    }

    public PowerStatePayload(LifxMessageType type, Power state) {
        super(type);
        this.state = state;
    }
    private PowerStatePayload(LifxMessageType type, byte[] data) {
        super(type, data);
    }

    public static PowerStatePayload newModificationCommand(Power newState) {
        PowerStatePayload res = new PowerStatePayload(LifxMessageType.REQ_SET_POWER_STATE, newState);
        return res;
    }
    public static PowerStatePayload fromRawDataResponse(byte[] data) {
        PowerStatePayload res = new PowerStatePayload(LifxMessageType.RESP_POWER_STATE, data);
        res.decodeFromRawdata();
        return res;
    }

    public Power getState() {
        return state;
    }

    @Override
    protected byte[] process2Bytes() {
        return this.state.value.getData();
    }

    private void decodeFromRawdata() {
        this.state = Power.fromByteValue(Arrays.copyOf(data, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PowerStatePayload that = (PowerStatePayload) o;

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
}
