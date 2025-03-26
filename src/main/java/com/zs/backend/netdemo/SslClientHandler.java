package com.zs.backend.netdemo;

/**
 * @Description
 * @Author Administrator
 * @Date 2025/3/25
 **/
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import java.util.function.Consumer;

public class SslClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 假设 msg 是一个字符串（或其他类型）
            String message = (String) msg;

            // 打印接收到的消息
            System.out.println("Received from server: " + message);

            // 解析消息并调用回调函数
            ResponseMessage response = parseResponse(message);

            // 获取请求ID
            String requestId = response.getRequestId();

            // 根据请求ID找到对应的回调函数
            Consumer<String> responseHandler = RequestContext.getResponseHandler(requestId);
            if (responseHandler != null) {
                responseHandler.accept(response.getData());
                RequestContext.removeRequest(requestId); // 清理上下文
            } else {
                System.out.println("未找到对应的请求ID: " + requestId);
            }
        } finally {
            // 确保释放消息资源
            ReferenceCountUtil.release(msg);
        }
    }

    private ResponseMessage parseResponse(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ResponseMessage.class);
    }
}
