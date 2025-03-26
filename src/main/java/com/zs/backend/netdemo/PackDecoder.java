package com.zs.backend.netdemo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2023-04-12
 * <p>
 * 数据包格式，遵循 Hp-Socket Pack 模式的报文格式
 * <p>
 * +——---10位---——+——----22位----——+——----——+
 * |   包头标志   |      包长度     |  数据  |
 * +——--------——+——------------——+——----——+
 */
@Slf4j
public class PackDecoder extends ByteToMessageDecoder {

    public final int BASE_LENGTH = 4;

    public final int TCP_PACK_LENGTH_BITS = 22;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        // 可读长度必须大于基本长度
        if (buffer.readableBytes() >= BASE_LENGTH) {

            // 记录包头开始的index
            int beginReader;

            // 数据包长度
            int dataLength;

            while (true) {

                // 获取包头开始的index
                beginReader = buffer.readerIndex();

                // 标记包头开始的index
                buffer.markReaderIndex();

                // 读取4字节的包头信息
                byte[] headerBytes = new byte[BASE_LENGTH];
                buffer.readBytes(headerBytes);
                ByteBuffer headBuf = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

                // 获取前32位包头
                int header = headBuf.getInt();

                // 提取Hp-Socket包头标识信息（前10位）
                int headerFlags = header >>> TCP_PACK_LENGTH_BITS;

                // 当读到了协议的开始标志时，提取数据长度，结束while循环
                // 当读到了协议的开始标志时，提取数据长度，结束while循环
                byte[] headByte = ByteUtil.intToBytes(HeaderUtil.HEADER_FLAG);
                if (headerFlags == ByteBuffer.wrap(headByte).order(ByteOrder.LITTLE_ENDIAN).getInt()) {
                    // 提取数据长度（后22位）
                    dataLength = header & 0x3FFFFF;
                    break;
                }

                // 每次略过一个字节，去读取包头信息的开始标记
                buffer.resetReaderIndex();

                // 当略过一个字节之后，数据包的长度小于基础长度时，等待后面的数据到达
                if (buffer.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            // 判断请求数据包数据是否到齐
            if (buffer.readableBytes() < dataLength) {
                // 若当前可读字节小于包数据长度，表示对端发生了拆包，还原读指针等待下次传输
                buffer.readerIndex(beginReader);
                return;
            }

            // 读取data数据  
            byte[] data = new byte[dataLength];
            buffer.readBytes(data);

            PackProtocol protocol = new PackProtocol(data.length, data);
            out.add(protocol);
        }
    }

}