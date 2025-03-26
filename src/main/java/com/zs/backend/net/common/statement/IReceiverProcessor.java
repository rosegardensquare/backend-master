package com.zs.backend.net.common.statement;


import com.zs.backend.net.common.statement.vo.ReceiveInfo;

/**
 * @author Tianyun
 * @description 消息接收处理接口
 * @create 2024-11-05 13:15
 **/

public interface IReceiverProcessor {

    /**
     * 处理对端发送的消息
     *
     * @param receiveInfo 接收信息
     */
    void process(ReceiveInfo receiveInfo);
}
