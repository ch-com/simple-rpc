package org.simple.core.exception;

/**
 * 方法调用没有可用的服务信息异常
 * @author ch
 * @date 2023/4/18
 */
public class NotFindAvailableServerException extends ClientException {
    public NotFindAvailableServerException(String message) {
        super(message);
    }
}
