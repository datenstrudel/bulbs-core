package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import java.util.Arrays;
import java.util.Optional;

public enum LifxPacketType {

    REQ_PAN_GATEWAY     (0x02, false),
    RESP_PAN_GATEWAY    (0x03, true),
    REQ_POWER_STATE     (0x14, false),
    REQ_SET_POWER_STATE (0x15, false),
    RESP_POWER_STATE    (0x16, true),
    ;

    private BT.Uint16 protocolValue;
    private boolean inbound;

    private LifxPacketType(int protcolValue, boolean inbound) {
        this.protocolValue = BT.Uint16.fromInt(protcolValue);
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
    public boolean isInbound() {
        return inbound;
    }
}
