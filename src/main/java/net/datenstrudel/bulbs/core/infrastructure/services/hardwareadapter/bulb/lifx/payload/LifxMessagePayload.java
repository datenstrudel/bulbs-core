package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxPacketType;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Created by Thomas Wendzinski.
 */
public abstract class LifxMessagePayload {

    protected byte[] data;
    protected LifxPacketType packetType;

    private LifxMessagePayload(){}
    protected LifxMessagePayload(LifxPacketType packetType) {
        this.packetType = packetType;
    }
    protected LifxMessagePayload(LifxPacketType packetType, byte[] data) {
        this.data = data;
        this.packetType = packetType;
    }
    /**
     * @param type The type of payload, denoted by the Lifx protocol
     * @param rawData binary payload representation, excluding message payload data
     * @return The decoded <code>LifxMessage</code> payload
     */
    public static LifxMessagePayload fromRawData(LifxPacketType type, byte[] rawData) {
        switch (type) {
            case REQ_PAN_GATEWAY:
                return LifxMessagePayload.emptyPayload(type);
            case RESP_PAN_GATEWAY:
                return new RespGetPanGateway(rawData);
            case RESP_POWER_STATE:
                return new RespPowerState(rawData);
            case RESP_LIGHT_STATE:
                return new RespLightState(rawData);
            case RESP_BULB_LABEL:
                return BulbLabelPayload.fromRawData(rawData);

            default:
                throw new UnsupportedOperationException("Cannot construct LifxMessagePayload by type: " + type);
        }
    }

    //~ ///////////////////////////////////////////////////////////////////
    public byte[] toBytes() {
        if (this.data == null)
            this.data = process2Bytes();
        if (this.data == null && !packetType.isInbound())
            throw new IllegalStateException("Binary data representation not found. Must be set on init! ");
        return this.data;
    }

    public int size() {
        return this.toBytes().length;
    }

    protected abstract byte[] process2Bytes();

    public LifxPacketType getPacketType() {
        return packetType;
    }

    protected void setData(byte[] data) {
        this.data = data;
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

    public static EmptyPayload emptyPayload(LifxPacketType packetType) {
        return new EmptyPayload(packetType);
    }
    public static class EmptyPayload extends LifxMessagePayload {
        public EmptyPayload(LifxPacketType packetType) {
            super(packetType, new byte[0]);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        protected byte[] process2Bytes() {
            return new byte[0];
        }
    }


}
