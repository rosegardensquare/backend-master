package com.zs.backend.test.net.impl;


import com.zs.backend.test.net.common.statement.IReceiverProcessor;
import com.zs.backend.test.net.common.statement.vo.ReceiveInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-05 14:04
 **/
@Slf4j
public class ClientReceiver implements IReceiverProcessor {

    private static final ClientReceiver instance = new ClientReceiver();

    private ClientReceiver() {
    }

    public static ClientReceiver getInstance() {
        return instance;
    }

    @Override
    public void process(ReceiveInfo receiveInfo) {
        System.err.println("ClientReceiver 接收到服务端 " + receiveInfo.getSourceIp() + " - " + receiveInfo.getConnId() + " 消息：" + receiveInfo.getData());
    }
}
