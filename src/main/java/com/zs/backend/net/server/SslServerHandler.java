package com.zs.backend.net.server;


import com.fasterxml.jackson.databind.JsonNode;
import com.zs.backend.net.common.queue.MessageQueue;
import com.zs.backend.net.common.statement.IReceiverProcessor;
import com.zs.backend.net.common.statement.coder.PackProtocol;
import com.zs.backend.net.common.statement.enums.MessageType;
import com.zs.backend.net.common.statement.util.JacksonUtil;
import com.zs.backend.net.common.statement.vo.ReceiveInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2023-04-12
 **/
@Slf4j
class SslServerHandler extends ChannelInboundHandlerAdapter {

    private final IReceiverProcessor receiverProcessor;
    private final SslServerConnection connectionManager = SslServerConnection.getInstance();

    SslServerHandler(IReceiverProcessor receiverProcessor) {
        this.receiverProcessor = receiverProcessor;
    }

    /**
     * 用于获取客户端发送的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            PackProtocol body = (PackProtocol) msg;
            String jsonString = new String(body.getContent(), StandardCharsets.UTF_8);
            JsonNode jsonObject = JacksonUtil.parseObject(jsonString, JsonNode.class);
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            int messageType = jsonObject.get("type").asInt();
            if (messageType == MessageType.HEART_BEAT.getCode()) {
                log.debug("【服务端】收到【客户端{}】心跳消息：{}",socketAddress.toString(),
                        jsonObject.get("data").asText());
            }

            // 处理客户端消息
            if (messageType == MessageType.CLIENT_MSG.getCode()) {
                log.debug("【服务端】收到【客户端{}】业务消息：{}",socketAddress.toString(),
                        jsonObject.get("data").asText());

                if (receiverProcessor != null) { // 1.若配置了处理类，则回调处理
                    ReceiveInfo receiveInfo = new ReceiveInfo()
                            .setSourceIp(socketAddress.getAddress().getHostAddress())
                            .setSourceIpPort(socketAddress.toString())
                            .setConnId(ctx.channel().id().asShortText())
                            .setData(jsonObject.get("data").asText());

                    receiverProcessor.process(receiveInfo);

                } else { // 2.未配置处理类，将消息发送至消息队列
                    boolean offer = MessageQueue.MSG_QUEUE.offer(jsonString);
                    if (!offer) {
                        log.warn("当前服务端消息队列已满: {}",socketAddress.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        int clientPort = socketAddress.getPort();
        log.error("与 客户端 连接断开【{}:{}】原因：{}", clientIp, clientPort, cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        String connId = channel.id().asShortText();

        connectionManager.addConnection(clientIp, connId, channel);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        String connId = channel.id().asShortText();

        connectionManager.removeConnection(clientIp, connId);
        super.channelInactive(ctx);
    }
}