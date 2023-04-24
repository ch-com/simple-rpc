package org.example;

import org.simple.core.domain.service.ServiceMetadata;
import org.simple.server.config.ServerConfig;
import org.simple.server.startup.NettyServer;
import org.simple.server.startup.RpcServer;
import org.simple.server.startup.registry.DefaultServiceRegistryImpl;

/**
 * @author ch
 * @date 2023/4/19
 */
public class ServerMain {
    public static void main(String[] args) {
        RpcServer server = new NettyServer(new DefaultServiceRegistryImpl(1));
        server.config(ServerConfig.defaultConfig());
        ServiceMetadata metadata = new ServiceMetadata();
        metadata.setVersion("1.0.0");
        metadata.setDefineClassName(ExampleService.class.getName());
        metadata.setServiceObject(new EchoImpl());
        server.registerService(metadata);
        server.start();
        System.out.println("server started");
    }
}
