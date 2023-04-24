package org.simple.client.request;

import com.sun.javafx.fxml.builder.URLBuilder;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.MessageBody;
import org.simple.core.domain.exchange.RequestBody;

/**
 * @author ch
 * @date 2023/4/17
 */
public class RpcRequest {
    /**
     * 请求id需保证全局唯一性
     */
    private String requestId;
    /**
     * 调用接口全限定名称
     */
    private String serviceInterface;
    /**
     * 调用方法
     */
    private String callMethod;
    /**
     * 调用方法参数
     */
    private Object[] methodParam;
    /**
     * 调用方法参数类型
     */
    private Class<?>[] methodParamType;
    /**
     * 重试次数
     */
    private int retryCount = 3;

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod;
    }

    public Object[] getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(Object[] methodParam) {
        this.methodParam = methodParam;
    }

    public Class<?>[] getMethodParamType() {
        return methodParamType;
    }

    public void setMethodParamType(Class<?>[] methodParamType) {
        this.methodParamType = methodParamType;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 将请求转换成消息
     * @return
     */
    public Message toRequestMessage() {
        Message message = new Message();
        message.setMesType(Message.Type.CALL);
        RequestBody body = new RequestBody();
        body.setRequestId(this.getRequestId());
        body.setRequestInterface(this.serviceInterface);
        body.setMethodParam(this.methodParam);
        body.setParamType(this.methodParamType);
        body.setServiceMethod(this.callMethod);
        body.setServiceVersion(this.version);
        message.setData(body);
        return message;
    }


}
