package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.StringJoiner;

public class MacAddress {

    private byte[] data;

    private MacAddress(byte[] data) {
        this.data = data;
    }

    public static MacAddress empty() {
        return new MacAddress(new byte[6]);
    }
    public static MacAddress fromBytes(byte[] mac) {
        if(mac.length != 6) throw new IllegalArgumentException("Invalid Mac address byte array: " + mac);
        return new MacAddress(mac);
    }
    public static MacAddress fromString(String mac) {
        if(mac == null || mac.isEmpty()) throw new IllegalArgumentException("Mac address must not be empty!");

        byte[] bytes = new BigInteger(mac.replaceAll(":", ""), 16).toByteArray();
        return new MacAddress(Arrays.copyOfRange(bytes, bytes.length > 6 ? 1 : 0, bytes.length));
    }

    public String toString(){
        StringJoiner res = new StringJoiner(":");
        for (byte b : data) {
            res.add(String.format("%02x", b));
        }
        return res.toString();
    }

    public byte[] getBytes() {
        return data;
    }

}
