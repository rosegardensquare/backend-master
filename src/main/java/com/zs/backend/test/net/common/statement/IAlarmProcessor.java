package com.zs.backend.test.net.common.statement;

/**
 * @author Tianyun
 * @description 告警接口
 * @create 2024-11-05 13:23
 **/

public interface IAlarmProcessor {

    /**
     * 产生告警
     *
     * @param address 服务端地址 格式 -> 服务IP:端口
     */
    void produce(String address);

    /**
     * 恢复告警
     *
     * @param address 服务端地址 格式 -> 服务IP:端口
     */
    void recover(String address);
}
