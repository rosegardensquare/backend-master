package com.zs.backend.net.server;

import com.zs.backend.net.common.statement.coder.PackProtocol;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SslServerConnection {

    private static final SslServerConnection INSTANCE = new SslServerConnection();

    // 客户端连接Map：IP -> (connId -> Channel)
    private final Map<String, Map<String, Channel>> CONNECTIONS_MAP = new ConcurrentHashMap<>();

    private SslServerConnection() {
    }

    public static SslServerConnection getInstance() {
        return INSTANCE;
    }

    void addConnection(String clientIp, String connId, Channel channel) {
        CONNECTIONS_MAP.computeIfAbsent(clientIp, k -> new ConcurrentHashMap<>()).put(connId, channel);
    }

    void removeConnection(String clientIp, String connId) {
        Map<String, Channel> connections = CONNECTIONS_MAP.get(clientIp);
        if (connections != null) {
            connections.remove(connId);
            if (connections.isEmpty()) {
                CONNECTIONS_MAP.remove(clientIp);
            }
        }
    }

    public void send(String clientIp, String connId, String data) {
        Map<String, Channel> connections = CONNECTIONS_MAP.get(clientIp);
        log.debug("SnmpTrap发送消息给客户端Ip:..." + clientIp + "  " + data);
        if (connections != null) {
            Channel targetChannel = connections.get(connId);
            if (targetChannel != null && targetChannel.isActive()) {
                // 获得要发送信息的字节数组
                byte[] content = data.getBytes();
                // 报文长度
                int contentLength = content.length;
                targetChannel.writeAndFlush(new PackProtocol(contentLength, content));
                log.info("SnmpTrap发送消息给客户端成功:..." + targetChannel.remoteAddress() + "  " + data);
            } else {
                log.error("SnmpTrap发送消息给客户端失败:..." + clientIp + "  " + data);
            }
        }
    }

    /**
     * 发送消息给所有客户端
     *
     * @param data
     */
    public void send(String data) {
        log.debug("SnmpTrap发送消息给客户端开始:..." + data);
        for (Map.Entry<String, Map<String, Channel>> entry : CONNECTIONS_MAP.entrySet()) {
            String clientIp = entry.getKey();
            Map<String, Channel> connections = entry.getValue();
            for (String key : connections.keySet()) {
                send(clientIp, key, data);
            }
        }
        log.debug("SnmpTrap发送消息给客户端结束:..." + data);
    }
}