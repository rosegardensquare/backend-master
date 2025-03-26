package com.zs.backend.test.net.common.statement.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-07 15:13
 **/
@Data
@Accessors(chain = true)
public class MessageVo {

    private Integer type;

    private String data;

}
