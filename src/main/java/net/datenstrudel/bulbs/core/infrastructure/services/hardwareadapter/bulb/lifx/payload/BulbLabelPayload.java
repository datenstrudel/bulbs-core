package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.BT;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.LifxMessageType;

import java.util.Arrays;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbLabelPayload extends LifxMessagePayload {

    private static final int MAX_LENGTH = 32;
    BT.CharArray bulbLabel;

    private BulbLabelPayload(LifxMessageType packetType) {
        super(packetType);
    }

    private BulbLabelPayload(LifxMessageType packetType, byte[] data) {
        super(packetType, data);
    }

    public static BulbLabelPayload newSetBulblabelPayload(String newLabel) {
        BulbLabelPayload res = new BulbLabelPayload(LifxMessageType.REQ_SET_BULB_LABEL);
        res.bulbLabel = BT.CharArray.fromString(newLabel);
        if(res.bulbLabel.getData().length > MAX_LENGTH) {
            res.bulbLabel = BT.CharArray.fromBytes(Arrays.copyOf(res.bulbLabel.getData(), MAX_LENGTH));
        }
        return res;
    }
    public static BulbLabelPayload fromRawData(byte[] payload) {
        BulbLabelPayload res = new BulbLabelPayload(LifxMessageType.RESP_BULB_LABEL, payload);
        res.bulbLabel = BT.CharArray.fromBytes(payload);
        return res;
    }

    public BT.CharArray getBulbLabel() {
        return bulbLabel;
    }
    public String getBulbLabelAsString() {
        return bulbLabel.toString();
    }

    @Override
    protected byte[] process2Bytes() {
        this.data = bulbLabel.getData();
        return data;
    }
}
