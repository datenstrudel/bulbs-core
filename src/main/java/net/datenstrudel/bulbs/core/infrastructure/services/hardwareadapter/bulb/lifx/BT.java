package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Lifx specific Binary Types
 */
public class BT {

    private static final Logger log = LoggerFactory.getLogger(BT.class);

    public static class Uint16{
        private byte data[];

        private Uint16(byte[] data) {
            if(data.length > 2) throw new IllegalArgumentException("Supplied byte array must not be greater than 2 bytes");
            if(data.length < 2) throw new IllegalArgumentException("Supplied byte array must not be smaller than 2 bytes");
            this.data = data;
        }
        public static Uint16 fromInt(int value) {
            ByteBuffer buf = ByteBuffer.allocate(4).putInt(value);
            Uint16 res = new Uint16(new byte[2]);
            buf.position(buf.capacity() - 2);
            buf.get(res.data, 0, 2);
            if(buf.array()[0] > 0 || buf.array()[1] > 0) log.warn("Bytes truncated on reading UInt16 from int {}", value);
            return res;
        }
        public static Uint16 fromBytes_LE(byte[] in) {
            if(in.length < 2) throw new IllegalArgumentException("Two bytes expected to parse UInt16 from LittleEndian input");
            if(in.length > 2)log.warn("Data too large on reading LE Uint16. Actual size was: {}", in.length);
            return new Uint16(new byte[]{in[1], in[0]});
        }
        public static Uint16 fromBytes(byte[] in) {
            return new Uint16(in);
        }

        public byte[] getData() {
            return data;
        }
        public byte[] getData_LE() {
            return new byte[]{data[1], data[0]};
        }

        public int toInt() {
            return ((ByteBuffer) ByteBuffer.allocate(4).put(new byte[2]).put(data).flip()).getInt();
        }

        @Override
        public String toString() {
            return "Uint16{" +
                    toInt() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Uint16 uint16 = (Uint16) o;
            if (!Arrays.equals(data, uint16.data)) return false;
            return true;
        }
        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }

    public static class Uint32{
        private byte data[];

        private Uint32(byte[] data) {
            if(data.length > 4) throw new IllegalArgumentException("Supplied byte array must not be greater than 4 bytes");
            if(data.length < 4) throw new IllegalArgumentException("Supplied byte array must not be smaller than 4 bytes");
            this.data = data;
        }

        public static Uint32 fromInt(int value) {
            Uint32 res = new Uint32(ByteBuffer.allocate(4).putInt(value).array());
            return res;
        }
        public static Uint32 fromBytes(byte[] bytes) {
            return new Uint32(bytes);
        }
        public static Uint32 fromBytes_LE(byte[] bytes) {
            return new Uint32(
                    ByteBuffer.allocate(4)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(new Uint32(bytes).toInt())
                            .array()
            );
        }

        public byte[] getData() {
            return data;
        }

        public int toInt() {
            return ByteBuffer.wrap(data).getInt();
        }

        @Override
        public String toString() {
            return "Uint32{" +
                    toInt() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Uint32 uint32 = (Uint32) o;
            if (!Arrays.equals(data, uint32.data)) return false;
            return true;
        }
        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }

    public static class Uint64{

        private byte data[];

        private Uint64(byte[] data) {
            if(data.length > 8) throw new IllegalArgumentException("Supplied byte array must not be greater than 2 bytes");
            if(data.length < 8) throw new IllegalArgumentException("Supplied byte array must not be smaller than 2 bytes");
            this.data = data;
        }
        public static Uint64 fromLong(long value) {
            Uint64 res = new Uint64(ByteBuffer.allocate(8).putLong(value).array());
            return res;
        }
        public static Uint64 fromBytes(byte[] bytes) {
            return new Uint64(bytes);
        }

        public byte[] getData() {
            return data;
        }
        public long toLong() {
            return ByteBuffer.wrap(data).getLong();
        }

        @Override
        public String toString() {
            return "Uint64{" +
                    toLong() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Uint64 uint64 = (Uint64) o;
            if (!Arrays.equals(data, uint64.data)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }

}
