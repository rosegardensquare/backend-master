package com.zs.backend.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Tianyun
 * @description
 * @create 2024-11-05 13:17
 **/
@EqualsAndHashCode(callSuper = true)
@Data
class ClientParam extends ConnectionVO {

    private String host;

    private Bootstrap bootstrap;

    private List<Channel> channels;
}
