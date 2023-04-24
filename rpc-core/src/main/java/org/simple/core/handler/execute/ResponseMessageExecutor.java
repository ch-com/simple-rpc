package org.simple.core.handler.execute;

import io.netty.channel.ChannelHandlerContext;
import org.simple.core.domain.exchange.ResponseBody;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应类消息执行
 * @author ch
 * @date 2023/4/20
 */
public interface ResponseMessageExecutor extends MessageExecutor {

    /**
     * 处理消息
     * @param channel 传输通道
     * @param requestHolder 待响应的请求列表
     * @param responseBody 响应消息
     */
    void execute(ChannelHandlerContext channel
            , ConcurrentHashMap<String, CompletableFuture<ResponseBody>> requestHolder
            , ResponseBody responseBody);

}
