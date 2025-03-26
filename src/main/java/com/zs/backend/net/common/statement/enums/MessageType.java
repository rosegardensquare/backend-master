package com.zs.backend.net.common.statement.enums;

import java.util.Objects;

/**
 * @author Tianyun
 * @description SSL消息类型枚举类
 * @create 2023-04-18
 **/

public enum MessageType {

    HEART_BEAT(100, "客户端心跳"),

    CLIENT_MSG(101, "客户端消息");

    private final String desc;
    private final int code;

    MessageType(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public static String getDesc(Integer code) {
        String res = null;
        MessageType[] enumAry = MessageType.values();
        for (MessageType typeEnum : enumAry) {
            if (Objects.equals(typeEnum.getCode(), code)) {
                res = typeEnum.desc;
                break;
            }
        }
        return res;
    }
}
