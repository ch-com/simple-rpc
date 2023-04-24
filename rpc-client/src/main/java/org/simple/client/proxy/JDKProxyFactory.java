package org.simple.client.proxy;

import cn.hutool.core.util.ArrayUtil;
import org.simple.client.NettyRpcClient;
import org.simple.client.discovery.ServiceProvider;
import org.simple.client.request.RpcRequest;
import org.simple.core.domain.exchange.ResponseBody;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * JDK实现的代理工厂
 * @author ch
 * @date 2023/4/23
 */
public class JDKProxyFactory<T> implements RpcProxyFactory<T>, InvocationHandler {
    private final ServiceProvider serviceProvider;
    private NettyRpcClient rpcClient = new NettyRpcClient();

    public JDKProxyFactory(ServiceProvider provider) {

        this.serviceProvider = provider;
        rpcClient.setServiceProvider(this.serviceProvider);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setCallMethod(method.getName());
        rpcRequest.setMethodParam(args);
        rpcRequest.setMethodParamType(method.getParameterTypes());
        rpcRequest.setVersion("1.0.0");
        rpcRequest.setServiceInterface(method.getDeclaringClass().getName());
        Object retObj = ((ResponseBody)rpcClient.call(rpcRequest)).getResponseData();
        if (retObj instanceof Throwable) {
            Throwable ex = Throwable.class.cast(retObj);
            throw ex;
        }
        return retObj;
    }

    @Override
    public T createProxy(Class<T> tClass) {
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {tClass}, this);
    }
}
