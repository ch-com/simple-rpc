package org.simple.core.domain.service;

import org.simple.core.domain.exchange.RequestBody;

/**
 * 服务端发布的接口信息
 * @author ch
 * @date 2023/4/17
 */
public class ServiceMetadata {
    /**
     * 发布的服务版本
     */
    private String version;
    /**
     * 服务接口定义
     */
    private String defineClassName;
    /**
     * 服务的具体实现
     */
    private Object serviceObject;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDefineClassName() {
        return defineClassName;
    }

    public void setDefineClassName(String defineClassName) {
        this.defineClassName = defineClassName;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public String getServiceKey() {
        return buildKey(this.defineClassName, this.version);
    }

    public static String buildKeyFromRequestBody(RequestBody requestBody) {
        return buildKey(requestBody.getRequestInterface(), requestBody.getServiceVersion());
    }

    private static String buildKey(String defineClassName, String version) {
        StringBuilder builder = new StringBuilder();
        if (defineClassName == null || "".equals(version.trim())) {
            throw new IllegalArgumentException("defineClassName property is null");
        }
        builder.append(defineClassName.trim());
        if (version == null || "".equals(version.trim())) {
            throw new IllegalArgumentException("version property is null");
        }
        builder.append(version.trim());
        return builder.toString();
    }
}
