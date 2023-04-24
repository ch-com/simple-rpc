package org.simple.core.handler.execute;

import io.netty.channel.ChannelHandlerContext;
import org.simple.core.domain.exchange.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用方法返回处理器
 * @author ch
 * @date 2023/4/20
 */
public class CallRetMessageExecutor implements ResponseMessageExecutor {
    private static final Logger log = LoggerFactory.getLogger(CallRetMessageExecutor.class);
    @Override
    public void execute(ChannelHandlerContext channel
            , ConcurrentHashMap<String, CompletableFuture<ResponseBody>> requestHolder
            , ResponseBody responseBody) {
        if (responseBody == null) {
            log.error("ResponseBody is null");
        }
        CompletableFuture<ResponseBody> requestFuture = requestHolder.get(responseBody.getRequestId());
        if (requestFuture == null) {
            log.error("Message response exception {}", responseBody);
            return;
        }
        requestFuture.complete(responseBody);
        requestHolder.remove(responseBody.getRequestId());
    }
}
