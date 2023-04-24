package org.simple.server.startup;

import org.simple.server.config.ServerConfig;
import org.simple.core.domain.service.ServiceMetadata;

public interface RpcServer {
    /**
     * 配置rpc服务实例
     * @param serverConfig 服务配置
     */
    void config(ServerConfig serverConfig);

    /**
     * 注册发布的服务信息
     * @param serviceMetadata 发布服务的元数据信息
     * @return
     */
    boolean registerService(ServiceMetadata serviceMetadata);

    /**
     * 启动服务端发布服务
     * @return
     */
    void start();



}
