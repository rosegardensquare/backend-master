package com.zs.backend.net;

import com.zs.backend.net.common.statement.util.PropUtil;
import com.zs.backend.net.common.statement.vo.CertVo;
import com.zs.backend.net.impl.ServerReceiver;
import com.zs.backend.net.server.ServerParam;
import com.zs.backend.net.server.SslServer;

/**
 * @Description
 * @Author Administrator
 * @Date 2025/3/26
 **/
public class NetServerTest {

    private static final int SERVER_PORT = 10009;

    public static void main(String[] args) {
        CertVo certVo = new CertVo()
            .setCertPath(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/server-cert.pem"))
            .setKeyPath(PropUtil.findFile(PropUtil.class.getClassLoader(), "cert/server-key.pem"));

        ServerParam param = new ServerParam()
            .setListenPort(SERVER_PORT)     // 监听端口
            .setReceiverProcessor(ServerReceiver.getInstance())    // 声明处理函数实现类
            .setCertVo(certVo);     // 自定义证书路径

        try {
            SslServer.getInstance().bind(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
