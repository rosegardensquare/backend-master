package com.zs.backend.net.common.queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Tianyun
 * @description
 * @create 2023-06-07
 **/

public class MessageQueue {

    public static final LinkedBlockingQueue<String> MSG_QUEUE = new LinkedBlockingQueue<>(100_000);

}
