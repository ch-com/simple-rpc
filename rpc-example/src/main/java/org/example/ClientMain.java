package org.example;

import org.simple.client.NettyRpcClient;
import org.simple.client.RpcClient;
import org.simple.client.discovery.MemoryServiceProvider;
import org.simple.client.discovery.ServiceProvider;
import org.simple.client.proxy.JDKProxyFactory;
import org.simple.client.request.RpcRequest;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.domain.service.RegisterServiceMetadata;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ch
 * @date 2023/4/22
 */
public class ClientMain {
    public static AtomicInteger allSend = new AtomicInteger(0); // 发送次数
    public static AtomicInteger succssResponse = new AtomicInteger(0); // 成功响应次数
    public static int a = 1000; // 单线程迭代次数
    public static void main(String[] args) throws NoSuchMethodException, InterruptedException {
        MemoryServiceProvider provider = new MemoryServiceProvider();
        RegisterServiceMetadata registerServiceMetadata = new RegisterServiceMetadata();
        registerServiceMetadata.setServiceInterface(ExampleService.class.getName());
        registerServiceMetadata.setServerIP("127.0.0.1");
        registerServiceMetadata.setServiceVersion("1.0.0");
        registerServiceMetadata.setServerPort(8088);
        provider.addService(registerServiceMetadata);
        int threadNum = 100;
        CountDownLatch count = new CountDownLatch(threadNum);
        // 模拟并发
        ExampleService service = new JDKProxyFactory<ExampleService>(provider).createProxy(ExampleService.class);
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    int j = 0;
                    while (j < a) {
                        String param = "hello" + index;
                        String expectRetStr = param + "1"; // 预期返回值
                        allSend.incrementAndGet();
                        String retStr = service.hello(param);
                        if (expectRetStr.equals(retStr)) {
                            succssResponse.incrementAndGet();
                        }
                        j++;
                    }
                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                } finally {
                    count.countDown();
                }
            });
        }
        Arrays.stream(threads).forEach(thread -> thread.start());
        count.await();
        System.out.println("all send request -> " + allSend.get() + " success Ressponse -> " +  succssResponse.get());

    }

    public static RpcRequest creatRequest(String param) throws NoSuchMethodException {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setCallMethod("hello");
        rpcRequest.setMethodParam(new Object[] {param});
        rpcRequest.setMethodParamType(ExampleService.class.getMethod("hello", String.class).getParameterTypes());
        rpcRequest.setVersion("1.0.0");
        rpcRequest.setServiceInterface(ExampleService.class.getName());
        return rpcRequest;
    }
}
