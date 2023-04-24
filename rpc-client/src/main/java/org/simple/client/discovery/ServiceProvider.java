package org.simple.client.discovery;


import org.simple.client.request.RpcRequest;
import org.simple.core.domain.service.RegisterServiceMetadata;

import java.util.List;
import java.util.Set;

/**
 * @author ch
 * @date 2023/4/18
 */
public interface ServiceProvider {
    /**
     * 查询匹配的服务信息
     * @param request
     * @return
     */
    Set<RegisterServiceMetadata> matchService(RpcRequest request);
}
