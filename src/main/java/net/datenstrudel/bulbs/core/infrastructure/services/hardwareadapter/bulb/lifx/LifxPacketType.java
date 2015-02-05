package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import java.util.Arrays;
import java.util.Optional;

public enum LifxPacketType {

    REQ_PAN_GATEWAY     (0x02, false, true),
    RESP_PAN_GATEWAY    (0x03, true, true),
    REQ_POWER_STATE     (0x14, false),
    REQ_SET_POWER_STATE (0x15, false),
    RESP_POWER_STATE    (0x16, true),
    ;

    private final BT.Uint16 protocolValue;
    private final boolean inbound;
    private boolean yieldsManyResponsePackets = false;

    private LifxPacketType(int protocolValue, boolean inbound) {
        this.protocolValue = BT.Uint16.fromInt(protocolValue);
        this.inbound = inbound;
    }
    private LifxPacketType(int protocolValue, boolean inbound, boolean yieldsManyResponsePackets) {
        this.protocolValue = BT.Uint16.fromInt(protocolValue);
        this.yieldsManyResponsePackets = yieldsManyResponsePackets;
        this.inbound = inbound;
    }
    public static LifxPacketType fromProtocolValue(int value) {
        Optional<LifxPacketType> res = Arrays.stream(LifxPacketType.values())
                .filter(v -> v.getProtocolValue().toInt() == value)
                .findFirst();
        if(!res.isPresent()) throw new IllegalArgumentException("Couldn't find LifxPacketType by protocol value: " + value);
        return res.get();
    }

    public BT.Uint16 getProtocolValue() {
        return protocolValue;
    }
    public boolean isYieldsManyResponsePackets() {
        return yieldsManyResponsePackets;
    }
    public boolean isInbound() {
        return inbound;
    }

    @Override
    public String toString() {
        return this.name() + "{" +
                "yieldsManyResponsePackets=" + yieldsManyResponsePackets +
                '}';
    }
}
