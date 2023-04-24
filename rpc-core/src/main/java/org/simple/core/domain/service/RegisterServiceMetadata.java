package org.simple.core.domain.service;

import java.util.Objects;

/**
 * 服务端提供的服务信息
 * @author ch
 * @date 2023/4/18
 */
public class RegisterServiceMetadata {
    /**
     * 服务主机ip
     */
    private String serverIP;
    /**
     * 服务主机端口
     */
    private Integer serverPort;
    /**
     * 服务发布的接口
     */
    private String serviceInterface;
    /**
     * 服务版本信息
     */
    private String serviceVersion;

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterServiceMetadata that = (RegisterServiceMetadata) o;
        return serverIP.equals(that.serverIP) && serverPort.equals(that.serverPort)
                && serviceInterface.equals(that.serviceInterface) && serviceVersion.equals(that.serviceVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverIP, serverPort, serviceInterface, serviceVersion);
    }

    public boolean validate() {
        if (this.serverIP == null) {
            return false;
        }
        if (this.serverPort == null) {
            return false;
        }
        if (this.serviceInterface == null) {
            return false;
        }
        if (this.serviceVersion == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RegisterServiceMetadata{" +
                "serverIP='" + serverIP + '\'' +
                ", serverPort=" + serverPort +
                ", serviceInterface='" + serviceInterface + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                '}';
    }
}
