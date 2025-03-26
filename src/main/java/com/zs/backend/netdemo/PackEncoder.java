package com.zs.backend.netdemo;

import com.zs.backend.netdemo.PackProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
public class PackEncoder extends MessageToByteEncoder<PackProtocol> {

    @Override
    protected void encode(ChannelHandlerContext tcx, PackProtocol msg, ByteBuf out) {
        // 1.写入消息的开头的信息标志固定32位
        out.writeBytes(msg.getHeadData());
        // 2.写入消息的内容
        out.writeBytes(msg.getContent());
    }
}