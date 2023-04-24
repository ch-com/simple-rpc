package org.simple.core.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.MessageBody;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.domain.service.ServiceRegistry;
import org.simple.core.handler.execute.CallMethodMessageExecutor;
import org.simple.core.handler.execute.CallRetMessageExecutor;
import org.simple.core.handler.execute.MessageExecutor;
import org.simple.core.handler.execute.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 消息处理器
 * @author ch
 * @date 2023/4/20
 */
public class MessageHander extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(MessageHander.class);
    // 服务发布信息
    private final ServiceRegistry registry;
    // 等待响应请求
    private final ConcurrentHashMap<String, CompletableFuture<ResponseBody>> requestHolder;
    public MessageHander(ServiceRegistry registry, ConcurrentHashMap<String, CompletableFuture<ResponseBody>> requestHolder) {
        super();
        this.registry = registry;
        this.requestHolder = requestHolder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestBody) {
            CallMethodMessageExecutor messageExecutor = CallMethodMessageExecutor.class
                    .cast(SingletonFactory.getExecutor(Message.Type.CALL.getCode()));
            ctx.executor().parent().execute(() -> {
                messageExecutor.execute(ctx, registry, (RequestBody)msg);
            });

        } else if (msg instanceof ResponseBody) {
            CallRetMessageExecutor messageExecutor = CallRetMessageExecutor.class
                    .cast(SingletonFactory.getExecutor(Message.Type.RESPONSE.getCode()));
            ctx.executor().parent().execute(() -> {
                messageExecutor.execute(ctx, requestHolder, (ResponseBody)msg);
            });

        }
        else {
            logger.error("Received Exception message", msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }
}
