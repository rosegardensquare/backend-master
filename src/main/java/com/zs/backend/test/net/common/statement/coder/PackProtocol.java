package com.zs.backend.test.net.common.statement.coder;


import com.zs.backend.test.net.common.statement.util.HeaderUtil;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    /**
     * 消息长度
     */
    private final int contentLength;

    /**
     * 消息内容
     */
    private final byte[] content;

    /**
     * 初始化
     *
     * @param contentLength 协议消息数据长度
     * @param content       协议消息数据
     */
    public PackProtocol(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public byte[] getHeadData() {
        return HeaderUtil.packHeader(contentLength);
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "PackProtocol [head_data=" + Arrays.toString(getHeadData()) + ", contentLength="
                + contentLength + ", content=" + new String(content, StandardCharsets.UTF_8) + "]";
    }

    public byte[] toByteArray() {
        byte[] result = new byte[4 + contentLength];
        System.arraycopy(intToBytes(contentLength), 0, result, 0, 4);
        System.arraycopy(content, 0, result, 4, contentLength);
        return result;
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }


}