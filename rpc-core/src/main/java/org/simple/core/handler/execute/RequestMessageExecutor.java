package org.simple.core.handler.execute;


import io.netty.channel.ChannelHandlerContext;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.service.ServiceRegistry;

import java.nio.channels.Channel;

/**
 * 请求类消息执行
 * @author ch
 * @date 2023/4/20
 */
public interface RequestMessageExecutor extends MessageExecutor {
    /**
     * 处理请求
     * @param channel 消息传输通道
     * @param registry 发布的服务注册表
     * @param request 消息请求体
     */
    void execute(ChannelHandlerContext channel, ServiceRegistry registry, RequestBody request);
}
