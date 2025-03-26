package com.zs.backend.test.net.common.statement.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-07 16:41
 **/
@Data
@Accessors(chain = true)
public class ReceiveInfo {

    private String sourceIpPort;

    private String sourceIp;

    private String connId;

    private String data;

}
