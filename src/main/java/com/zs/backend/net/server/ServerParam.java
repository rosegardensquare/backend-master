package com.zs.backend.net.server;

import com.zs.backend.net.common.statement.IReceiverProcessor;
import com.zs.backend.net.common.statement.vo.CertVo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-07 14:03
 **/
@Data
@Accessors(chain = true)
public class ServerParam {

    /**
     * 监听端口
     */
    private Integer listenPort;

    /**
     * 消息接收处理类，不设置则默认使用 消息队列 接收消息
     */
    private IReceiverProcessor receiverProcessor;

    /**
     * 证书信息
     */
    private CertVo certVo;

}
