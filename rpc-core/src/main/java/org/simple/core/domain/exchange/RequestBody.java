package org.simple.core.domain.exchange;

import java.io.Serializable;

/**
 * @author ch
 * @date 2023/4/18
 */
public class RequestBody implements Serializable, MessageBody {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String requestInterface;
    private String serviceVersion;
    private String serviceMethod;
    private Object[] methodParam;
    private Class<?>[] paramType;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestInterface() {
        return requestInterface;
    }

    public void setRequestInterface(String requestInterface) {
        this.requestInterface = requestInterface;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Object[] getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(Object[] methodParam) {
        this.methodParam = methodParam;
    }

    public Class<?>[] getParamType() {
        return paramType;
    }

    public void setParamType(Class<?>[] paramType) {
        this.paramType = paramType;
    }
}
