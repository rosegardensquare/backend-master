package com.zs.backend.net.client;


import com.zs.backend.net.common.statement.IAlarmProcessor;
import com.zs.backend.net.common.statement.IReceiverProcessor;
import com.zs.backend.net.common.statement.vo.CertVo;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Tianyun
 * @description 连接参数VO
 * @create 2024-11-05 13:26
 **/
@Data
@Accessors(chain = true)
public class ConnectionVO {

    /**
     * 服务端 IP集合
     */
    private List<String> serverIps;

    /**
     * 服务端 端口
     */
    private int serverPort;

    /**
     * 客户端证书信息
     */
    private CertVo certVo;

    /**
     * 告警开关，不设置该值，默认关闭告警
     */
    private boolean alarmSwitch;

    /**
     * 服务端消息处理接口
     */
    private IReceiverProcessor receiverProcessor;

    /**
     * 告警处理接口
     */
    private IAlarmProcessor alarmProcessor;
}
