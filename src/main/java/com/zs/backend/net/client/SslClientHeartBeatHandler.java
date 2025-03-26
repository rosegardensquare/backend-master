package com.zs.backend.net.client;


import com.zs.backend.netdemo.PackProtocol;
import com.zs.backend.net.common.statement.enums.MessageType;
import com.zs.backend.net.common.statement.util.JacksonUtil;
import com.zs.backend.net.common.statement.vo.MessageVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.nio.charset.StandardCharsets;

/**
 * @author Tianyun
 * @description SSL心跳
 * @create 2024-11-05 09:54
 **/
public class SslClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            // 写空闲 事件
            if (state == IdleState.WRITER_IDLE) {

                MessageVo messageVo = new MessageVo()
                        .setType(MessageType.HEART_BEAT.getCode())
                        .setData("client heartbeat");

                byte[] content = JacksonUtil.toJSON(messageVo).getBytes(StandardCharsets.UTF_8);

                PackProtocol protocol = new PackProtocol(content.length, content);

                ctx.writeAndFlush(protocol);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}