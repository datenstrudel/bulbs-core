package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Created by Thomas Wendzinski.
 */
public abstract class LifxMessagePayload {

    protected byte[] data;

    public LifxMessagePayload() {
    }
    public LifxMessagePayload(byte[] data) {
        this.data = data;
    }
    /**
     * @param type The type of payload, denoted by the Lifx protocol
     * @param rawData binary payload representation, excluding message payload data
     * @return The decoded <code>LifxMessage</code> payload
     */
    public static LifxMessagePayload fromRawData(LifxPacketType type, byte[] rawData) {
        switch (type) {
            case REQ_PAN_GATEWAY:
                return LifxMessagePayload.emptyPayload();
            case RESP_PAN_GATEWAY:
                return new RespGetPanGateway(rawData);
            default:
                throw new UnsupportedOperationException("Cannot construct LifxMessagePayload by type: " + type);
        }
    }

    public byte[] toBytes() {
        return this.data;
    }
    public int size() {
        return this.data.length;
    }

    public String dataToString() {
        StringJoiner res = new StringJoiner(" : ");
        for(byte el : data) res.add(String.valueOf(el));
        return res.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LifxMessagePayload that = (LifxMessagePayload) o;
        if (!Arrays.equals(data, that.data)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? Arrays.hashCode(data) : 0;
    }

    public static EmptyPayload emptyPayload() {
        return new EmptyPayload();
    }
    public static class EmptyPayload extends LifxMessagePayload {
        public EmptyPayload() {
            super(new byte[0]);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }


}
