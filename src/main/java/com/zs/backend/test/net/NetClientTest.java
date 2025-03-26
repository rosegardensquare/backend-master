package com.zs.backend.test.net;

import cn.hutool.core.util.StrUtil;
import com.zs.backend.test.net.client.ConnectionVO;
import com.zs.backend.test.net.client.SslClient;
import com.zs.backend.test.net.client.SslClientBuilder;
import com.zs.backend.test.net.common.statement.enums.MessageType;
import com.zs.backend.test.net.common.statement.util.JacksonUtil;
import com.zs.backend.test.net.common.statement.util.PropUtil;
import com.zs.backend.test.net.common.statement.vo.CertVo;
import com.zs.backend.test.net.common.statement.vo.MessageVo;
import com.zs.backend.test.net.impl.AlarmProcessorImpl;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author Administrator
 * @Date 2025/3/26
 **/
public class NetClientTest {

    private static final int SERVER_PORT = 10009;

    private static final List<String> SERVER_IPS = Collections.singletonList("127.0.0.1");
    //private static final List<String> SERVER_IPS = Collections.singletonList("192.168.33.223");


    public static void main(String[] args) {

        CertVo certVo = new CertVo()
        .setCertPath(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/client-cert.pem"))
        .setKeyPath(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/client-key.pem"))
        .setCaPath(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/ca.pem"));

        ConnectionVO connectionVO = new ConnectionVO()
            .setServerIps(SERVER_IPS)       // (必填项) 服务IP集合
            .setServerPort(SERVER_PORT)     // (必填项) 服务端口
            .setCertVo(certVo)             // (选填项) 通信SSL证书，不设置则使用默认证书
            .setAlarmProcessor(AlarmProcessorImpl.getInstance());    // (选填项) 消息处理实现类，若不需要处理服务端消息，不设置即可
        connServerAndSendMsg(connectionVO, "666");

    }

    private static void connServerAndSendMsg(ConnectionVO connectionVO, String msg) {
        // 1.构建连接，返回连接成功的服务地址(格式 -> 服务IP:端口)
        String serverAddress = SslClientBuilder.getInstance().buildConnection(connectionVO);

        if (StrUtil.isNotBlank(serverAddress)) {

            // 2.获取可用连接
            SslClient sslClient = SslClientBuilder.getInstance().getSslClient(serverAddress);

            if (sslClient != null) {

                // 3.封装消息
                MessageVo messageVo = new MessageVo()
                    .setType(MessageType.CLIENT_MSG.getCode())
                    .setData(msg);

                // 4.发送消息
                sslClient.send(JacksonUtil.toJSON(messageVo));
            }
        } else {
            System.out.println("与服务端构建SSL失败");
        }
    }
}
