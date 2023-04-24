package org.simple.core.handler.execute;

import cn.hutool.core.util.ReflectUtil;
import io.netty.channel.ChannelHandlerContext;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.domain.exchange.ResponseStateEnum;
import org.simple.core.domain.service.ServiceMetadata;
import org.simple.core.domain.service.ServiceRegistry;
import org.simple.core.exception.NotPublishServiceException;

import java.lang.reflect.Method;
import java.nio.channels.Channel;

/**
 * 方法调用处理器
 * @author ch
 * @date 2023/4/20
 */
public class CallMethodMessageExecutor implements RequestMessageExecutor {
    @Override
    public void execute(ChannelHandlerContext channel, ServiceRegistry registry, RequestBody requestBody) {
        try {
            ServiceMetadata metadata = registry.selectByKey(ServiceMetadata.buildKeyFromRequestBody(requestBody));
            Method method = null;
            if (metadata == null) {
                // 响应服务未发布异常
                channel.writeAndFlush(Message.createMessage(Message.Type.RESPONSE, Message.SeriaType.PROTOSTUFF
                        , ResponseBody
                        .buildResponse(requestBody.getRequestId()
                        , new NotPublishServiceException(""), ResponseStateEnum.NOT_FIND
                        , "")));
            }
            method = ReflectUtil.getMethod(metadata.getServiceObject().getClass()
                    , requestBody.getServiceMethod(), requestBody.getParamType());
            if (method == null) {
                channel.writeAndFlush(Message.createMessage(Message.Type.RESPONSE, Message.SeriaType.PROTOSTUFF
                        , ResponseBody.buildResponse(requestBody.getRequestId()
                        , new NotPublishServiceException(""), ResponseStateEnum.NOT_FIND
                        , "notfind match method")));
            }
            Object retObj = method.invoke(metadata.getServiceObject(), requestBody.getMethodParam());
            channel.writeAndFlush(Message.createMessage(Message.Type.RESPONSE, Message.SeriaType.PROTOSTUFF
                    , ResponseBody.buildSuccessResponse(requestBody.getRequestId(), retObj)));
        } catch (Throwable e) {
            channel.writeAndFlush(Message.createMessage(Message.Type.RESPONSE, Message.SeriaType.PROTOSTUFF
                    , ResponseBody.buildFailResponse(requestBody.getRequestId()
                    , e
                    , e.getMessage())));
        }
    }
}
