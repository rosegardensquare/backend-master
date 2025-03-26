package com.zs.backend.netdemo;


/**
 * @author Tianyun
 * @description
 * @create 2023-04-12
 *
 * 数据包格式，遵循 Hp-Socket Pack 模式的报文格式
 *
 * +——---10位---——+——----22位----——+——----——+
 * |   包头标志   |      包长度     |  数据  |
 * +——--------——+——------------——+——----——+
 */

public class PackProtocol {

    private final int length; // 消息长度
    private final byte[] content; // 消息内容

    public PackProtocol(int length, byte[] content) {
        this.length = length;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[4 + length];
        System.arraycopy(intToBytes(length), 0, result, 0, 4);
        System.arraycopy(content, 0, result, 4, length);
        return result;
    }

    public static PackProtocol fromByteArray(byte[] data) {
        int length = bytesToInt(data, 0);
        byte[] content = new byte[length];
        System.arraycopy(data, 4, content, 0, length);
        return new PackProtocol(length, content);
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }

    private static int bytesToInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24) |
            ((data[offset + 1] & 0xFF) << 16) |
            ((data[offset + 2] & 0xFF) << 8) |
            (data[offset + 3] & 0xFF);
    }

    public byte[] getHeadData() {
        return HeaderUtil.packHeader(length);
    }
}