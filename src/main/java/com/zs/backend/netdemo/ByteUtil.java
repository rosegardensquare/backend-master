package com.zs.backend.netdemo;

import java.nio.ByteOrder;

public class ByteUtil {
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;

    public ByteUtil() {
    }

    /**
     * 小端模式 intToBytes:将int转为低字节在前，高字节在后的byte数组（小端）
     * @param intValue
     * @return
     */
    public static byte[] intToBytes(int intValue) {
        return intToBytes(intValue, DEFAULT_ORDER);
    }

    /**
     * intToBytes:将int转为低字节在前，高字节在后的byte数组（小端）
     * @param intValue
     * @param byteOrder
     * @return
     */
    public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {
        return ByteOrder.LITTLE_ENDIAN == byteOrder ? new byte[]{(byte)(intValue & 255), (byte)(intValue >> 8 & 255), (byte)(intValue >> 16 & 255), (byte)(intValue >> 24 & 255)} : new byte[]{(byte)(intValue >> 24 & 255), (byte)(intValue >> 16 & 255), (byte)(intValue >> 8 & 255), (byte)(intValue & 255)};
    }
}
