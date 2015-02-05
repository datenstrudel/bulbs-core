package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Thomas Wendzinski.
 */
public class RespGetPanGateway extends LifxMessagePayload {

    private int port;
    private Service service;

    public RespGetPanGateway(byte[] data) {
        super(data);
        decodeFromRawdata();
    }
    public RespGetPanGateway(int port, Service service) {
        this.port = port;
        this.service = service;
        byte[] rawData = ByteBuffer.allocate(5).put(service.getValue()).put(BT.Uint32.fromInt(port).getData_LE()).array();
        setData(rawData);
    }

    private void decodeFromRawdata() {
        this.service = Service.fromByteValue(data[0]);
        this.port = BT.Uint32.fromBytes_LE(Arrays.copyOfRange(data, 1,5)).toInt();
    }
    public static enum Service{
        UDP( (byte) 1),
        TCP( (byte) 2),
        ;
        private byte value;

        private Service(byte value) {
            this.value = value;
        }
        public static Service fromByteValue(byte value) {
            Optional<Service> res = Arrays.stream(Service.values()).filter(s -> s.value == value).findFirst();
            if(!res.isPresent()) throw new IllegalArgumentException("Service not found for given value: " + value);
            return res.get();
        }

        public byte getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return "RespGetPanGateway{" +
                "port=" + port +
                ", service=" + service +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RespGetPanGateway that = (RespGetPanGateway) o;

        if (port != that.port) return false;
        if (service != that.service) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + port;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }
}
