package com.zs.backend.net.client;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.zs.backend.net.common.statement.constant.GlobalConstant;
import com.zs.backend.net.common.statement.util.CacheUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import cn.hutool.core.thread.ThreadUtil;

/**
 * @author Tianyun
 * @description SSL客户端构建器
 * @create 2023-04-20
 **/
@Slf4j
public class SslClientBuilder {

    private static final SslClientBuilder instance = new SslClientBuilder();

    private List<String> serverIps;

    private Integer serverPort;

    private SslClientBuilder() {
    }

    public static SslClientBuilder getInstance() {
        return instance;
    }

    /**
     * SSL客户端连接缓存
     * key      服务IP:PORT
     * value    SSL连接客户端
     */
    private final Cache<String, SslClient> cache = CacheUtil.createCaffeineCache(100_000L, this.callbackListener());

    public SslClient getSslClient(String serverAddress) {
        return cache.getIfPresent(serverAddress);
    }

    public void removeSslClient(String serverAddress) {
        cache.invalidate(serverAddress);
    }

    /**
     * 连接断开，触发缓存失效，尝试重连
     */
    private RemovalListener<String, SslClient> callbackListener() {
        return (key, value, cause) -> {
            log.warn("与服务端【{}】SSL连接中断，移除 SSLClient 缓存，当前客户端连接数量 {}", key, cache.asMap().size());

            ConnectionVO connectionVO = new ConnectionVO().setServerIps(serverIps).setServerPort(serverPort);

            String serverAddress = this.buildConnection(connectionVO);

            while (StringUtils.isEmpty(serverAddress)) { // 未返回连接地址，一直尝试重连
                serverAddress = this.buildConnection(connectionVO);
                ThreadUtil.sleep(GlobalConstant.SLEEP_MILLISECOND);
            }
        };
    }

    /**
     * 与服务端构建连接
     *
     * @param vo 连接信息
     * @return serverAddress 连接成功的服务地址 格式 -> 服务IP:端口
     */
    public String buildConnection(ConnectionVO vo) {
        this.serverIps = vo.getServerIps();
        this.serverPort = vo.getServerPort();

        String serverAddress = null;
        boolean connResult = false;
        for (String serverIp : serverIps) {
            ClientParam param = new ClientParam();
            BeanUtils.copyProperties(vo, param);
            param.setHost(serverIp);

            SslClient client = new SslClient(param);

            // 尝试重连 10 次
            for (int i = 1; i <= 10 && !connResult; i++) {

                connResult = client.connect();

                if (!connResult) {

                    log.info("连接远端服务 {} 失败，进行第 {} 次重试", serverIp + ":" + serverPort, i);

                    ThreadUtil.sleep(GlobalConstant.SLEEP_MILLISECOND);
                }
            }

            if (connResult) {
                serverAddress = serverIp + ":" + serverPort;

                this.cache.put(serverAddress, client);

                log.info("连接远端服务 {} 成功客户端连接存入缓存，当前客户端连接数量 {}", serverAddress, cache.asMap().size());

                break;
            }
        }
        return serverAddress;
    }
}
