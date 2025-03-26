package com.zs.backend.netdemo;

import cn.hutool.core.util.StrUtil;
import com.zs.backend.test.net.common.statement.util.PropUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import java.io.ByteArrayInputStream;
import java.io.File;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 创建 Boss 和 Worker 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 负责接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 负责处理连接

        try {

            // 加载 SSL/TLS 上下文
            SslContext sslContext = loadSslContext();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // 使用 NIO 通道
                .childHandler(new ChildChannelHandler(sslContext))
                .option(ChannelOption.SO_BACKLOG, 128) // 设置连接队列大小
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持长连接

            // 绑定端口并启动服务端
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Server started and listening on port " + port);

            // 等待服务端关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * 消息处理器
     */
    private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;


        private ChildChannelHandler(SslContext sslContext){
            this.sslContext = sslContext;
        }
        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            // 添加 SSL/TLS 处理器

            pipeline.addLast(sslContext.newHandler(ch.alloc()));

            // 添加编解码器（字符串格式）
            pipeline.addLast(new StringDecoder());
            pipeline.addLast(new StringEncoder());

            // 添加自定义处理器
            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                    System.out.println("Received from client: " + msg);

                    // 向客户端发送响应
                    String response = "{\"data\":\"666\"}";
                    ctx.writeAndFlush(response);
                    System.out.println("response from server: " + response);
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                    cause.printStackTrace();
                    ctx.close();
                }
            });
        }
    }



    private SslContext loadSslContext() throws Exception {
        // 加载证书和私钥
        File certFile = new File(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/server-cert.pem"));
        File keyFile = new File(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/server-key.pem"));

        return SslContextBuilder.forServer(certFile, keyFile).build();
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 10009; // 监听的端口号
        try {
            new NettyServer(port).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}