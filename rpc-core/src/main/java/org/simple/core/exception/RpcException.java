package org.simple.core.exception;

/**
 * @author ch
 * @date 2023/4/18
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

}
