package com.zs.backend.net.impl;


import com.zs.backend.net.common.statement.IReceiverProcessor;
import com.zs.backend.net.common.statement.enums.MessageType;
import com.zs.backend.net.common.statement.util.JacksonUtil;
import com.zs.backend.net.common.statement.vo.MessageVo;
import com.zs.backend.net.common.statement.vo.ReceiveInfo;
import com.zs.backend.net.server.SslServerConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-05 14:04
 **/
@Slf4j
public class ServerReceiver implements IReceiverProcessor {

    private static final ServerReceiver instance = new ServerReceiver();

    private ServerReceiver() {
    }

    public static ServerReceiver getInstance() {
        return instance;
    }

    @Override
    public void process(ReceiveInfo receiveInfo) {
        System.err.println("ServerReceiver 接收到客户端 " + receiveInfo.getSourceIp() + " - " + receiveInfo.getConnId() + " 消息：" + receiveInfo.getData());

        MessageVo msg = new MessageVo()
                .setType(MessageType.CLIENT_MSG.getCode())
                .setData("I am SSL Server");

        // 通过 SslServerConnection 向客户端发送消息
        SslServerConnection.getInstance().send(receiveInfo.getSourceIp(), receiveInfo.getConnId(), JacksonUtil.toJSON(msg));
    }
}
