package com.zs.backend.net.server;

import cn.hutool.core.util.StrUtil;
import com.zs.backend.net.common.statement.util.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description SSL服务端
 * @create 2024-11-05 09:54
 **/
@Slf4j
public class SslServer {

    private static final SslServer instance = new SslServer();

    private SslServer() {
    }

    public static SslServer getInstance() {
        return instance;
    }

    public void bind(ServerParam param) throws Exception {
        // 配置NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务器辅助启动类配置  
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChildChannelHandler(param))
                    .childOption(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    .option(ChannelOption.SO_BACKLOG, 128);

            // 绑定端口 同步等待绑定成功
            ChannelFuture f = b.bind(param.getListenPort()).sync();
            log.info("开始监听Tcp Socket Ssl {}端口成功!   ",param.getListenPort());
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 消息处理器
     */
    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        private final SslContext sslContext;

        private final ServerParam param;

        private ChildChannelHandler(ServerParam param) throws IOException {
            InputStream certInputStream = param.getCertVo() != null && StrUtil.isNotBlank(param.getCertVo().getCertPath()) ?
                    new FileInputStream(param.getCertVo().getCertPath()) : getClass().getResourceAsStream("/cert/server-cert.pem");

            InputStream keyInputStream = param.getCertVo() != null && StrUtil.isNotBlank(param.getCertVo().getKeyPath()) ?
                    new FileInputStream(param.getCertVo().getKeyPath()) : getClass().getResourceAsStream("/cert/server-key.pem");

            byte[] certBytes = CommonUtil.toByteArray(certInputStream);
            byte[] keyBytes = CommonUtil.toByteArray(keyInputStream);

            SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(new ByteArrayInputStream(certBytes), new ByteArrayInputStream(keyBytes));
            this.sslContext = sslContextBuilder.build();
            this.param = param;
        }

        @Override
        protected void initChannel(SocketChannel ch) {
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
//            ch.pipeline().addLast(new PackEncoder());
//            ch.pipeline().addLast(new PackDecoder());

            ch.pipeline().addLast(new StringDecoder());
            ch.pipeline().addLast(new StringEncoder());
            ch.pipeline().addLast(new IdleStateHandler(60, 0, 0));
            ch.pipeline().addLast(new HeartbeatServerHandler());
            // 处理网络IO
            ch.pipeline().addLast(new SslServerHandler(param.getReceiverProcessor()));
        }
    }
}