package com.zs.backend.netdemo;

import com.zs.backend.net.common.statement.util.PropUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SslClient {

    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public void start(String host, int port) throws Exception {
        // 加载 SSL/TLS 上下文
        SslContext sslContext = loadSslContext();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 添加 SSL/TLS 处理器
                    // pipeline.addLast(sslContext.newHandler(ch.alloc()));


//                    pipeline.addLast(new PackEncoder());
//                    pipeline.addLast(new PackDecoder());

                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new SslClientHandler());
                }
            });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        channel = future.channel();
    }

    public String sendAndWait(String message) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> responseRef = new AtomicReference<>();

        send(message, response -> {
            responseRef.set(response);
            latch.countDown();
        });

        boolean success = latch.await(10, TimeUnit.SECONDS);
        if (!success) {
            throw new RuntimeException("请求超时，未收到服务端响应");
        }

        return responseRef.get();
    }

    public void send(String message, Consumer<String> responseHandler) {
        if (channel != null && channel.isActive()) {
            byte[] content = message.getBytes();
            PackProtocol protocol = new PackProtocol(content.length, content);
            String requestId = java.util.UUID.randomUUID().toString();

            RequestContext.setRequestId(requestId);
            RequestContext.setResponseHandler(requestId, responseHandler);

            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(protocol.toByteArray());

            channel.writeAndFlush(buffer)
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        System.err.println("发送失败: " + future.cause());
                        RequestContext.removeRequest(requestId);
                    }else{
                        System.err.println("发送成功");
                    }
                });
        } else {
            System.err.println("通道不可用，无法发送消息");
        }
    }

    /**
     * 加载 SSL/TLS 上下文
     */
    private SslContext loadSslContext() throws Exception {
        // 加载信任证书（如果是自签名证书，需要提供服务器的公钥证书）
        File certFile = new File(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/client-cert.pem"));


        return SslContextBuilder.forClient()
            .trustManager(certFile) // 信任服务器的证书
            .build();
    }

    public void stop() {
        group.shutdownGracefully();
    }


    public static void main(String[] args) throws InterruptedException {
        SslClient client = new SslClient();

        try {
            client.start("localhost", 10009);
            String response = client.sendAndWait("777");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }
}