package com.zs.backend.test.net.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.backend.netdemo.ResponseMessage;
import com.zs.backend.test.net.common.statement.IAlarmProcessor;
import com.zs.backend.test.net.common.statement.IReceiverProcessor;
import com.zs.backend.test.net.common.statement.coder.PackDecoder;
import com.zs.backend.test.net.common.statement.coder.PackEncoder;
import com.zs.backend.test.net.common.statement.coder.PackProtocol;
import com.zs.backend.test.net.common.statement.constant.GlobalConstant;
import com.zs.backend.test.net.common.statement.util.JacksonUtil;
import com.zs.backend.test.net.common.statement.vo.CertVo;
import com.zs.backend.test.net.common.statement.vo.ReceiveInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2023-04-12
 **/
@Slf4j
@Sharable
public abstract class SslClientHandler extends ChannelInboundHandlerAdapter implements TimerTask, FireChannelHandler {

    private final HashedWheelTimer timer = new HashedWheelTimer();
    private static final int RETRY_CONN_TIME = 10;
    private int reconnectCount = 1;
    private final Bootstrap bootstrap;
    private final String host;
    private final int port;
    private final List<Channel> channels;
    private volatile boolean BOOT_FLAG = false;
    private final IReceiverProcessor receiverProcessor;
    private final IAlarmProcessor alarmProcessor;
    private final boolean alarmSwitch;
    private final CertVo certVo;

    public SslClientHandler(ClientParam param) {
        this.receiverProcessor = param.getReceiverProcessor();
        this.alarmProcessor = param.getAlarmProcessor();
        this.alarmSwitch = param.isAlarmSwitch();
        this.bootstrap = param.getBootstrap();
        this.channels = param.getChannels();
        this.port = param.getServerPort();
        this.host = param.getHost();
        this.certVo = param.getCertVo();
    }

    /**
     * SSL通道激活
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("与服务端链路已激活，尝试重连次数已重置  ctx is {}", ctx.toString());

        reconnectCount = 1;

        BOOT_FLAG = true;

        ctx.fireChannelActive();
    }

    /**
     * 接收服务端请求指令
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 假设 msg 是一个字符串（或其他类型）
            String message = (String) msg;
            // 打印接收到的消息
            System.out.println("Received from server: " + message);

            // 解析消息并调用回调函数
            ResponseMessage response = parseResponse(message);
            if(response != null){
                if(alarmProcessor != null){
                    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

                    alarmProcessor.produce(socketAddress.getAddress().getHostAddress());

                }
                // 消息接收器处理服务端消息
                if (receiverProcessor != null) {
                    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                    ReceiveInfo receiveInfo = new ReceiveInfo()
                        .setSourceIp(socketAddress.getAddress().getHostAddress())
                        .setSourceIpPort(socketAddress.toString())
                        .setConnId(ctx.channel().id().asShortText())
                        .setData(response.getData());

                    receiverProcessor.process(receiveInfo);
                }
            }

            PackProtocol body = (PackProtocol) msg;

            log.debug("接收到服务端消息：{}  ctx is {}", body, ctx.toString());

            String jsonString = new String(body.getContent(), StandardCharsets.UTF_8);
            JsonNode jsonObject = JacksonUtil.parseObject(jsonString, JsonNode.class);

            if (jsonObject != null) {
                // 消息接收器处理服务端消息
                if (receiverProcessor != null) {
                    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                    ReceiveInfo receiveInfo = new ReceiveInfo()
                            .setSourceIp(socketAddress.getAddress().getHostAddress())
                            .setSourceIpPort(socketAddress.toString())
                            .setConnId(ctx.channel().id().asShortText())
                            .setData(jsonObject.get("data").asText());

                    receiverProcessor.process(receiveInfo);
                }
            }

        } catch (Exception e) {
            log.error("接收服务端请求异常：" + "  ctx is  " + ctx.toString(), e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private ResponseMessage parseResponse(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ResponseMessage.class);
    }

    /**
     * SSL通道关闭，自动进行重连
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (BOOT_FLAG) {

            String address = host + ":" + port;

            // 产生告警
            if (alarmSwitch) {
                alarmProcessor.produce(address);
            }

            log.info("与服务端【{}】SSL-Channel通道关闭，将进行第 " + reconnectCount + " 次重连  ctx is {} ", address, ctx);

            if (reconnectCount < 5) {

                reconnectCount++;

                timer.newTimeout(this, RETRY_CONN_TIME, TimeUnit.SECONDS);
            } else {

                timer.stop();

                // 移除失效的SSL连接
                SslClient sslClient = SslClientBuilder.getInstance().getSslClient(address);
                if (sslClient != null) {
                    sslClient.close();

                    SslClientBuilder.getInstance().removeSslClient(address);
                }
            }
        } else {
            timer.stop();
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws IOException {
        ChannelFuture channelFuture;
        // SslContext sslCtx = CommonUtil.getClientSslCtx(certVo);

        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                   // pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                    pipeline.addLast(new PackEncoder());
                    pipeline.addLast(new PackDecoder());
                    pipeline.addLast(new IdleStateHandler(0, GlobalConstant.HEART_BEAT_TIME, 0, TimeUnit.SECONDS));
                    pipeline.addLast(new SslClientHeartBeatHandler());
                    pipeline.addLast(channelHandler());
                }
            });
            channelFuture = bootstrap.connect(host, port);
        }

        String address = host + ":" + port;

        //添加重连结果监听
        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            if (channelFuture1.isSuccess()) {

                channels.set(0, channelFuture.channel());

                // 恢复告警
                if (alarmSwitch) {
                    alarmProcessor.recover(address);
                }

                log.info("与服务端【{}】SSL-Channel通道重连成功  ctx is {}", address,channelFuture.channel().toString());
            } else {
                channelFuture1.channel().pipeline().fireChannelInactive();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
//        String serverIp = socketAddress.getAddress().getHostAddress();
//        int serverPort = socketAddress.getPort();
        log.error("与服务端连接断开【{}】原因：{}", ctx.toString(), cause.getMessage());
        ctx.close();
    }
}