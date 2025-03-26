package com.zs.backend.net.client;


import com.zs.backend.net.common.statement.IAlarmProcessor;
import com.zs.backend.net.common.statement.coder.PackProtocol;
import com.zs.backend.net.common.statement.constant.GlobalConstant;
import com.zs.backend.net.common.statement.util.CommonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SslClient {

    private static volatile boolean CONNECTION_STATE = false;

    public List<Channel> channels = Collections.synchronizedList(new ArrayList<>(1));
    private Bootstrap bootstrap;
    private final String host;
    private final int port;
    private final boolean alarmSwitch;
    private final IAlarmProcessor alarmProcessor;

    public SslClient(ClientParam param) {
        this.port = param.getServerPort();
        this.host = param.getHost();
        this.alarmSwitch = param.isAlarmSwitch();
        this.alarmProcessor = param.getAlarmProcessor();
        try {
            SslContext sslCtx = CommonUtil.getClientSslCtx(param.getCertVo());

            // 配置客户端NIO线程组
            EventLoopGroup group = new NioEventLoopGroup();

            // 客户端辅助启动类 对客户端配置
            this.bootstrap = new Bootstrap();
            param.setBootstrap(bootstrap);
            param.setChannels(channels);
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
//                            pipeline.addLast(new PackEncoder());
//                            pipeline.addLast(new PackDecoder());
                        pipeline.addLast(new IdleStateHandler(0, GlobalConstant.HEART_BEAT_TIME, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new SslClientHeartBeatHandler());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new SslClientHandler(param) {
                            @Override
                            public ChannelHandler channelHandler() {
                                return this;
                            }
                        });
                    }
                });
        } catch (Exception e) {
            log.error("SslClint客户端初始化异常：", e);
        }
    }

    /**
     * 连接服务器
     */
    boolean connect() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            synchronized (SslClient.class) {

                // 连接服务器并等待连接结果
                ChannelFuture future = bootstrap.connect(host, port);

                String serverAddress = host + ":" + port;

                future.addListener((ChannelFutureListener) channelFuture1 -> {
                    if (channelFuture1.isSuccess()) {

                        channels.add(future.channel());

                        CONNECTION_STATE = true;

                        if (alarmSwitch) { // 恢复告警
                            alarmProcessor.recover(serverAddress);
                        }

                        latch.countDown();
                    } else {
                        channelFuture1.channel().pipeline().fireChannelInactive();

                        CONNECTION_STATE = false;

                        if (alarmSwitch) { // 产生告警
                            alarmProcessor.produce(serverAddress);
                        }

                        latch.countDown();
                    }
                });
            }

            // 阻塞等待返回连接结果
            latch.await();
        } catch (Exception e) {
            log.error("SslClint客户端构建连接异常：", e);
        }
        return CONNECTION_STATE;
    }

    /**
     * 发送消息
     *
     * @param message 消息体
     */
    public void send(String message) {
        Channel channel = this.getChannel();
        if (channel != null && channel.isActive()) {
            // 获得要发送信息的字节数组
            byte[] content = message.getBytes();

            // 报文长度
            int contentLength = content.length;

            // log.debug("SSL客户端发送消息长度：{} 消息内容：{}", contentLength, message);

            // 封装协议包
            PackProtocol protocol = new PackProtocol(contentLength, content);
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(protocol.toByteArray());
            channel.writeAndFlush(buffer)
                    .addListener(future -> {
                        if (!future.isSuccess()) {
                            log.error("SSL客户端发送消息异常：{}   ctx is {}", message,channel.toString());
                        }
                    });
        } else {
            log.warn("消息发送失败，远程连接状态异常，Data -> {}", message);
        }
    }

    /**
     * 获取连接ID
     *
     * @return 连接ID
     */
    public ChannelId getChannelId() {
        ChannelId channelId = null;
        if (channels.size() > 0) {
            channelId = channels.get(0).id();
        }
        return channelId;
    }

    private Channel getChannel() {
        Channel channel = null;
        if (channels.size() > 0) {
            channel = channels.get(0);
        }
        return channel;
    }

    /**
     * 关闭连接
     */
    public void close() {
        Channel channel = this.getChannel();
        if (channel != null) {
            channel.close();
        }
        bootstrap.group().shutdownGracefully();
    }
}