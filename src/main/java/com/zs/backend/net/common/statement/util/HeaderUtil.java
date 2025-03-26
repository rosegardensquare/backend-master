package com.zs.backend.net.common.statement.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Tianyun
 * @description
 * @create 2023-04-12
 **/

public class HeaderUtil {

    public static final int HEADER_FLAG = 0x76;

    public static final int MAX_PACKET_LENGTH = 0x3FFFFF;

    /**
     * 封装Pack模式的包头
     *
     * @param bodyLength 数据包长度，单位为字节
     * @return 包头字节数组
     */
    public static byte[] packHeader(int bodyLength) {
        if (bodyLength > MAX_PACKET_LENGTH) {
            throw new IllegalArgumentException("Packet length exceeds the maximum value of 22 bits");
        }

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节序为小端
        buffer.putInt((HEADER_FLAG << 22) | bodyLength);

        return buffer.array();
    }
}
