package org.simple.core.exception;

import io.protostuff.Rpc;

/**
 * 服务端产生的异常基类
 * @author ch
 * @date 2023/4/20
 */
public class ServerException extends RpcException {
    public ServerException(String message) {
        super(message);
    }
}
