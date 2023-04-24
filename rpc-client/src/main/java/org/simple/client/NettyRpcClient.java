package org.simple.client;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.simple.client.discovery.ServiceProvider;
import org.simple.client.request.RpcRequest;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.domain.service.RegisterServiceMetadata;
import org.simple.core.exception.ClientException;
import org.simple.core.exception.ClientParamException;
import org.simple.core.exception.NotFindAvailableServerException;
import org.simple.core.handler.inbound.MessageDecoder;
import org.simple.core.handler.inbound.MessageHander;
import org.simple.core.handler.outbound.MessageEncoder;
import org.simple.core.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 采用netty客户端连接
 * @author ch
 * @date 2023/4/18
 */
public class NettyRpcClient implements RpcClient{
    Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);
    private volatile Bootstrap baseBoot;
    private NioEventLoopGroup eventLoopGroup;
    // 服务提供者
    private ServiceProvider serviceProvider;

    private ConcurrentHashMap<InetSocketAddress, Channel> channelHashMap = new ConcurrentHashMap<>();
    // 请求缓存容器
    private final ConcurrentHashMap<String, CompletableFuture<ResponseBody>> requestHolder = new ConcurrentHashMap<>();

    public NettyRpcClient(ServiceProvider serviceProvider) {
        this();
        this.serviceProvider = serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public NettyRpcClient() {
        this.init();
    }

    private void init() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder(Integer.MAX_VALUE, MessageEncoder.LENGTH_OFFSET, MessageEncoder.LENGTH_SIZE));
                        pipeline.addLast(new MessageHander(null, requestHolder));
                    }
                });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            eventLoopGroup.shutdownGracefully();
        }));
        baseBoot = bootstrap;
    }

    private Channel createConnect(InetSocketAddress inetSocketAddress) {
        try {
            return baseBoot.connect(inetSocketAddress).sync().channel();
        } catch (InterruptedException e) {
            throw new ClientException("Connect exception");
        }
    }

    @Override
    public Object call(RpcRequest rpcRequest) {
        // 参数校验
        this.valid(rpcRequest);
        Set<RegisterServiceMetadata> serviceMetadataSet = serviceProvider.matchService(rpcRequest);
        if (CollectionUtil.isEmpty(serviceMetadataSet)) {
            throw new NotFindAvailableServerException("No service information available");
        }
        RegisterServiceMetadata serviceMetadata = serviceMetadataSet.stream().findFirst().get();
        InetSocketAddress address = new InetSocketAddress(serviceMetadata.getServerIP(), serviceMetadata.getServerPort());
        Channel channel = channelHashMap.computeIfAbsent(address, k -> {
            logger.info("create channel -> " + address);
            return createConnect(k);
        });
        Message message = rpcRequest.toRequestMessage();
        // 设置消息序列化器类型
        message.setSeriaTypeEnum(Message.SeriaType.PROTOSTUFF);
        CompletableFuture<ResponseBody> responseFuture = new CompletableFuture<>();
        try {
            if (channel.isActive()) {
                this.send(channel, responseFuture, message, rpcRequest);
                // 同步等待响应请求
            } else {
                // 连接关闭进行重试
                channelHashMap.remove(address);
                if (rpcRequest.getRetryCount() > 0) {
                    rpcRequest.setRetryCount(rpcRequest.getRetryCount() - 1);
                    return this.call(rpcRequest);
                } else {
                    throw new ClientException("rpc message send fail");
                }
            }
            return responseFuture.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            throw new ClientException("response timeout");
        } catch (Throwable e) {
            throw new ClientException("rpc message send fail");
        }
    }

    private void send(Channel channel, CompletableFuture<ResponseBody> response, Message sendMsg, RpcRequest request) throws InterruptedException {
        // 将请求放到容器里
        requestHolder.put(((RequestBody)sendMsg.getData()).getRequestId(), response);
        channel.writeAndFlush(sendMsg).sync().addListener(future -> {
            if (!future.isSuccess()) {
                requestHolder.remove(((RequestBody)sendMsg.getData()).getRequestId());
                if (request.getRetryCount() > 0) {
                    // 重发请求
                    request.setRetryCount(request.getRetryCount() - 1);
                    this.send(channel, response, sendMsg, request);
                } else {
                    throw new ClientException("rpc message send fail");
                }
            }
        });
    }

    @Override
    public void close() {
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void valid(RpcRequest rpcRequest) {
        if (rpcRequest == null) {
            throw new ClientParamException("rpcRequest is null");
        }
        if (rpcRequest.getVersion() == null) {
            throw new ClientParamException("rpcRequest.version is null");
        }
        if (rpcRequest.getRequestId() == null) {
            throw new ClientParamException("rpcRequest.requestId is null");
        }
        if (rpcRequest.getCallMethod() == null) {
            throw new ClientParamException("rpcRequest.callMethod is null");
        }
        if (rpcRequest.getServiceInterface() == null) {
            throw new ClientParamException("rpcRequest.serviceInterface is null");
        }
        if (!ReflectUtil.compareObjectType(rpcRequest.getMethodParam(), rpcRequest.getMethodParamType())) {
            throw new ClientParamException("rpcRequest call Method mismatching");
        }
    }


}
