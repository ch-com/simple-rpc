package org.simple.server.startup;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.simple.core.handler.inbound.MessageDecoder;
import org.simple.core.handler.inbound.MessageHander;
import org.simple.core.handler.outbound.MessageEncoder;
import org.simple.server.config.ServerConfig;
import org.simple.core.domain.service.ServiceMetadata;
import org.simple.server.startup.registry.DefaultServiceRegistryImpl;
import org.simple.core.domain.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ch
 * @date 2023/4/17
 */
public class NettyServer implements RpcServer {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private ServerConfig config = ServerConfig.defaultConfig();
    private volatile ServiceRegistry serviceRegistry;

    public NettyServer(ServiceRegistry serviceRegistry) {
        if (serviceRegistry == null) {
            this.serviceRegistry = new DefaultServiceRegistryImpl(16);
        }
        this.serviceRegistry = serviceRegistry;
    }

    public NettyServer() {
        this(null);
    }

    @Override
    public void config(ServerConfig serverConfig) {
        this.config = serverConfig;
    }

    @Override
    public boolean registerService(ServiceMetadata serviceMetadata) {
        return this.serviceRegistry.add(serviceMetadata);
    }

    @Override
    public void start() {
        EventLoopGroup group = new NioEventLoopGroup(config.getMainGroupThread());
        EventLoopGroup childGroup = new NioEventLoopGroup(config.getChildGroupThread());
        EventExecutorGroup executors = new DefaultEventExecutorGroup(config.getRpcThread()
                , ThreadFactoryBuilder.create().setNamePrefix("rpc-execute-").build());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, config.getSoBacklog());

            bootstrap.group(group, childGroup).channel(NioServerSocketChannel.class)
                    // 开启心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 禁用nagle算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 定义netty日志基本
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder(Integer.MAX_VALUE, MessageEncoder.LENGTH_OFFSET, MessageEncoder.LENGTH_SIZE));
                            pipeline.addLast(executors, new MessageHander(serviceRegistry, null));
                        }
                    });

            bootstrap.bind(config.getPort()).sync().channel().closeFuture().sync();
        } catch (InterruptedException exception) {
            logger.error("append interrupt: ", exception);
        } finally {
            group.shutdownGracefully();
            childGroup.shutdownGracefully();
            executors.shutdownGracefully();
        }
    }
}
