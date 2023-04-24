package org.simple.client.proxy;

/**
 * 代理工厂接口
 * @author ch
 * @date 2023/4/23
 */
public interface RpcProxyFactory<T> {
    /**
     * 创建代理实例
     * @param tClass
     * @return
     */
    T createProxy(Class<T> tClass);
}
