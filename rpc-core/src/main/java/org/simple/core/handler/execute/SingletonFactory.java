package org.simple.core.handler.execute;

import org.simple.core.domain.exchange.Message;

import java.util.HashMap;

/**
 * @author ch
 * @date 2023/4/20
 */
public abstract class SingletonFactory {
    private static final HashMap<Byte, MessageExecutor> executorHashMap = new HashMap<>();
    static {

        executorHashMap.put(Message.Type.CALL.getCode(), new CallMethodMessageExecutor());
        executorHashMap.put(Message.Type.RESPONSE.getCode(), new CallRetMessageExecutor());
    }

    /**
     * 通过消息类型获取对应执行器实例
     * @param messType
     * @return
     */
    public static MessageExecutor getExecutor(Byte messType) {
        return executorHashMap.get(messType);
    }
}
