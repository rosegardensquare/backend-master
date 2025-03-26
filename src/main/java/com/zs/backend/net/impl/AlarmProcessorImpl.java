package com.zs.backend.net.impl;

import com.zs.backend.net.common.statement.IAlarmProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-05 14:04
 **/
@Slf4j
public class AlarmProcessorImpl implements IAlarmProcessor {

    private static final AlarmProcessorImpl instance = new AlarmProcessorImpl();

    private AlarmProcessorImpl() {
    }

    public static AlarmProcessorImpl getInstance() {
        return instance;
    }

    @Override
    public void produce(String address) {
        System.err.println("【产生】SSL连接中断告警：" + address);
    }

    @Override
    public void recover(String address) {
        System.err.println("【恢复】SSL连接中断告警：" + address);
    }
}
