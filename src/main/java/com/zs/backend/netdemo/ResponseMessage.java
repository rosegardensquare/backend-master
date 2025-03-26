package com.zs.backend.netdemo;

/**
 * @Description
 * @Author Administrator
 * @Date 2025/3/25
 **/
public class ResponseMessage {
    private String requestId;
    private String data;

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
