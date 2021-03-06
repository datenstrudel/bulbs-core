package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.StringJoiner;

/**
 * /**
 * The message whose binary representation is supposed to be sent over a network channel
 *
 * @param <T> extends LifxMessagePayload the actual payload type, depending on <code>packet_type</code>, e.g. {@link LifxMessageType LifxPacketType}
 */
public class LifxMessage<T extends LifxMessagePayload> {

    public static final Logger log = LoggerFactory.getLogger(LifxMessage.class);
    public static final int SIZE_HEADER = 36;

    // ~ ///////////////////////////////////////////////////////////////////////////////////
    private BT.Uint16 size;                                            // LE
    private BT.Uint16 protocol          = BT.Uint16.fromInt(52);       // the actual version - 52
    private BT.Uint32 reserved1         = BT.Uint32.fromInt(0);        // Always 0x0000
    private MacAddress target_mac_address = MacAddress.empty();
    private BT.Uint16 reserved2         = BT.Uint16.fromInt(0);
    private MacAddress site             = MacAddress.empty();          // MAC address of gateway PAN controller bulb
    private byte[] routing              = new byte[2];                 // Always 0x00
    private BT.Uint64 timestamp         = BT.Uint64.fromLong(new Date(0l).getTime());
    private BT.Uint16 packet_type;                                     // LE
    private BT.Uint16 reserved3         = BT.Uint16.fromInt(0);

    //~ //////////////////////////////////////////////////////////////////////////////////
    private T payload;           // Documented below per packet type

    //~ //////////////////////////////////////////////////////////////////////////////////
    private byte[] rawData = null;

    //~ Meta data (not part of message body //////////////////////////////////////////////
    private InetAddress address;
    private int port;

    //~ //////////////////////////////////////////////////////////////////////////////////
    public LifxMessage() {
    }

    public LifxMessage(T payload) {
        this.packet_type = payload.getPacketType().getProtocolValue();
        this.payload = payload;
        setSize(calcSize());
    }
    @Deprecated
    public LifxMessage(T payload, InetAddress address, int port, String targetMacAddress ) {
        this.packet_type = payload.getPacketType().getProtocolValue();
        this.payload = payload;
        this.address = address;
        this.port = port;
//        this.target_mac_address = targetMacAddress;
        setSite( MacAddress.fromString(targetMacAddress));
        setSize(calcSize());
    }

    protected static <T extends LifxMessagePayload> LifxMessage<T>  messageFrom(
            T payload, InetAddress address, int port, MacAddress gatewayMacAddress, MacAddress targetMacAddress ) {
        LifxMessage res = new LifxMessage(payload);
        res.packet_type = payload.getPacketType().getProtocolValue();
        res.payload = payload;
        res.address = address;
        res.port = port;
        res.setSite( gatewayMacAddress );
        res.setTarget_mac_address(targetMacAddress);
        res.setSize(res.calcSize());
        return res;
    }
    protected static <T extends LifxMessagePayload> LifxMessage<T>  messageFrom(
            T payload, InetAddress address, int port, String targetMacAddress ) {
        LifxMessage res = new LifxMessage(payload);
        res.packet_type = payload.getPacketType().getProtocolValue();
        res.payload = payload;
        res.address = address;
        res.port = port;
        res.setSite(targetMacAddress != null &&
                targetMacAddress.length() > 0
                ? MacAddress.fromString(targetMacAddress)
                : MacAddress.empty());
        res.setSize(res.calcSize());
        return res;
    }
    protected static <T extends LifxMessagePayload> LifxMessage<T>  messageFrom(
            T payload, InetAddress address, int port ) {
        LifxMessage res = new LifxMessage(payload);
        res.packet_type = payload.getPacketType().getProtocolValue();
        res.payload = payload;
        res.address = address;
        res.port = port;
        res.setSize(res.calcSize());
        return res;
    }
    protected static <T extends LifxMessagePayload> LifxMessage<T> messageFrom(T payload ) {
        LifxMessage res = new LifxMessage(payload);
        res.packet_type = payload.getPacketType().getProtocolValue();
        res.payload = payload;
        res.setSize(res.calcSize());
        return res;
    }


    @Deprecated
    public LifxMessage(LifxMessageType packetType, T payload) {
        this.packet_type = packetType.getProtocolValue();
        this.payload = payload;
        setSize(calcSize());
    }
    public LifxMessage(LifxMessageType packetType, T payload, InetAddress address, int port) {
        this.packet_type = packetType.getProtocolValue();
        this.payload = payload;
        this.address = address;
        this.port = port;
        setSize(calcSize());
    }
    public static LifxMessage fromBytes(byte[] input, InetAddress senderAddress, int senderPort) {
        LifxMessage res = new LifxMessage();
        res.rawData = input;

        byte[] tmp;
        ByteBuffer buffer = ByteBuffer.wrap(input);

        //~ Decode header
        tmp = new byte[2];
        buffer.get(tmp);
        res.size = BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        buffer.get(tmp);
        res.protocol = BT.Uint16.fromBytes(tmp);

        tmp = new byte[4];
        buffer.get(tmp);
        res.reserved1 = BT.Uint32.fromBytes(tmp);

        tmp = new byte[6];
        buffer.get(tmp);
        res.target_mac_address = MacAddress.fromBytes(tmp);

        tmp = new byte[2];
        buffer.get(tmp);
        res.reserved2 = BT.Uint16.fromBytes(tmp);

        tmp = new byte[6];
        buffer.get(tmp);
        res.site = MacAddress.fromBytes(tmp);

        buffer.get(res.routing);

        tmp = new byte[8];
        buffer.get(tmp);
        res.timestamp = BT.Uint64.fromBytes(tmp);

        tmp = new byte[2];
        buffer.get(tmp);
        res.packet_type= BT.Uint16.fromBytes_LE(tmp);

        tmp = new byte[2];
        buffer.get(tmp);
        res.reserved3 = BT.Uint16.fromBytes_LE(tmp);

        //~ Decode Payload
        res.payload = LifxMessagePayload.fromRawData(
                LifxMessageType.fromProtocolValue(res.packet_type.toInt()),
                Arrays.copyOfRange(input, SIZE_HEADER, input.length));
        res.address = senderAddress;
        res.port = senderPort;
        return res;
    }

    public byte[] toBytes() {
        if(this.rawData != null) return rawData;
        ByteBuffer res = ByteBuffer.allocate(this.size.toInt());
        res.put(BT.Uint16.fromInt(res.capacity()).getData_LE() );
        res.put(this.protocol.getData());
        res.put(this.reserved1.getData());
        res.put(this.target_mac_address.getBytes());
        res.put(this.reserved2.getData());
        res.put(this.site.getBytes());
        res.put(this.routing);
        res.put(this.timestamp.getData());
        res.put(this.packet_type.getData_LE());
        res.put(this.reserved3.getData());
        res.put(this.payload.toBytes());
        this.rawData = res.array();
        return res.array();
    }

    public String rawDataToString(){
        StringJoiner res = new StringJoiner(" : ");
        for(byte el : rawData) res.add(String.valueOf(el));
        return res.toString();
    }

    public MacAddress getTarget_mac_address() {
        return target_mac_address;
    }
    public MacAddress getGatewayMacAddress() {
        return this.site;
    }
    public T getPayload() {
        return payload;
    }
    public InetAddress getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }
    public LifxMessageType getType() {
        return LifxMessageType.fromProtocolValue(packet_type.toInt());
    }

    private void setSize(int size) {
        this.size = BT.Uint16.fromInt(size);
    }
    private int calcSize() {
        return this.payload.size() + SIZE_HEADER;
    }

    private void setSite(MacAddress site) {
        this.site = site;
    }
    private void setTarget_mac_address(MacAddress target_mac_address) {
        this.target_mac_address = target_mac_address;
    }

    @Override
    public String toString() {
        return "LifxMessage{" +
                "packet_type=" + LifxMessageType.fromProtocolValue(packet_type.toInt()) +
                ", size=" + size +
                ", protocol=" + protocol.toInt() +
                ", target_mac_address=" + target_mac_address +
                ", site=" + site +
                ", routing=" + Arrays.toString(routing) +
                ", timestamp=" + Instant.ofEpochMilli(timestamp.toLong()).toString() +
                ", payload=" + payload +
                ", rawData=" + Arrays.toString(rawData) +
                '}';
    }
    public void toStringConsole() {
        log.info("|-- LifxMessage{" );
        log.info(" -- packet_type=" + LifxMessageType.fromProtocolValue(packet_type.toInt()) );
        log.info(" -- size=" + size.toInt() );
        log.info(" -- protocol=" + protocol.toInt() );
        log.info(" -- target_mac_address=" + target_mac_address );
        log.info(" -- site=" + site );
        log.info(" -- routing=" + Arrays.toString(routing) );
        log.info(" -- timestamp=" + Instant.ofEpochMilli(timestamp.toLong()).toString() );
        log.info(" -- payload=" + payload );
        log.info(" -- rawData=" + Arrays.toString(rawData) );
        log.info(" -- }");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LifxMessage that = (LifxMessage) o;

        if (packet_type != null ? !packet_type.equals(that.packet_type) : that.packet_type != null) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
        if (reserved1 != null ? !reserved1.equals(that.reserved1) : that.reserved1 != null) return false;
        if (reserved2 != null ? !reserved2.equals(that.reserved2) : that.reserved2 != null) return false;
        if (reserved3 != null ? !reserved3.equals(that.reserved3) : that.reserved3 != null) return false;
        if (!Arrays.equals(routing, that.routing)) return false;
        if (!Arrays.equals(site.getBytes(), that.site.getBytes())) return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;
        if (!Arrays.equals(target_mac_address.getBytes(), that.target_mac_address.getBytes())) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = size != null ? size.hashCode() : 0;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (reserved1 != null ? reserved1.hashCode() : 0);
        result = 31 * result + (target_mac_address != null ? Arrays.hashCode(target_mac_address.getBytes()) : 0);
        result = 31 * result + (reserved2 != null ? reserved2.hashCode() : 0);
        result = 31 * result + (site != null ? Arrays.hashCode(site.getBytes()) : 0);
        result = 31 * result + (routing != null ? Arrays.hashCode(routing) : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (packet_type != null ? packet_type.hashCode() : 0);
        result = 31 * result + (reserved3 != null ? reserved3.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
