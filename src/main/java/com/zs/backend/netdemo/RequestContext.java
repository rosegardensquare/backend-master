package com.zs.backend.netdemo;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RequestContext {
    // 存储请求ID与回调函数的映射关系
    private static final Map<String, Consumer<String>> RESPONSE_HANDLERS = new ConcurrentHashMap<>();

    // 使用 ThreadLocal 存储当前线程的请求ID
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    /**
     * 设置当前线程的请求ID
     */
    public static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    /**
     * 获取当前线程的请求ID
     */
    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    /**
     * 设置请求ID与回调函数的映射关系
     */
    public static void setResponseHandler(String requestId, Consumer<String> handler) {
        RESPONSE_HANDLERS.put(requestId, handler);
    }

    /**
     * 根据请求ID获取回调函数
     */
    public static Consumer<String> getResponseHandler(String requestId) {
        return RESPONSE_HANDLERS.get(requestId);
    }

    /**
     * 移除请求ID及其对应的回调函数
     */
    public static void removeRequest(String requestId) {
        RESPONSE_HANDLERS.remove(requestId);
    }
}