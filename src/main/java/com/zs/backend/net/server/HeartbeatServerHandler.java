package com.zs.backend.net.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) { // 60秒 未收到客户端心跳，则主动释放连接
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                String clientIp = socketAddress.getAddress().getHostAddress();
                int clientPort = socketAddress.getPort();
                log.info("心跳监听【{}:{}】客户端心跳超时，服务端主动关闭当前连接", clientIp, clientPort);
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}