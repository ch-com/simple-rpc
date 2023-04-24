package org.simple.server.config;

import jdk.nashorn.internal.objects.annotations.Getter;

/**
 * @author ch
 * @date 2023/4/17
 */
public class ServerConfig {
    /**
     * 监听端口
     */
    private int port;
    /**
     * 接收连接线程数
     */
    private int mainGroupThread;
    /**
     * 处理读写事件线程数
     */
    private int childGroupThread;

    /**
     * 定义处理rpc请求线程数
     */
    private int rpcThread;
    /**
     * 连接缓存数
     */
    private int soBacklog;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMainGroupThread() {
        return mainGroupThread;
    }

    public void setMainGroupThread(int mainGroupThread) {
        this.mainGroupThread = mainGroupThread;
    }

    public int getChildGroupThread() {
        return childGroupThread;
    }

    public void setChildGroupThread(int childGroupThread) {
        this.childGroupThread = childGroupThread;
    }

    public int getRpcThread() {
        return rpcThread;
    }

    public void setRpcThread(int rpcThread) {
        this.rpcThread = rpcThread;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    /**
     * 生成默认配置
     * @return
     */
    public static ServerConfig defaultConfig() {
        ServerConfig config = new ServerConfig();
        config.setPort(8088);
        config.setChildGroupThread(4);
        config.setMainGroupThread(1);
        config.setRpcThread(8);
        return config;
    }
}
