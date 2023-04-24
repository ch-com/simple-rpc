package org.simple.client;

import org.simple.client.request.RpcRequest;

/**
 * @author ch
 * @date 2023/4/17
 */
public interface RpcClient {
    /**
     * 调用方法
     * @param rpcRequest
     * @return
     */
    Object call(RpcRequest rpcRequest);

    void close();
}
