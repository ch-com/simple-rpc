package org.simple.core.exception;

/**
 * 客户端产生的异常基类
 * @author ch
 * @date 2023/4/18
 */
public class ClientException extends RpcException {

    public ClientException(String message) {
        super(message);
    }

}
