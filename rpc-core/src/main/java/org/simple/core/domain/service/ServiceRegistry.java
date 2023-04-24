package org.simple.core.domain.service;


import java.util.Set;

/**
 * 服务注册表接口，提供对服务列表的增删查
 * @author ch
 * @date 2023/4/17
 */
public interface ServiceRegistry {
    /**
     * 查询服务信息
     * @param key
     * @return
     */
    ServiceMetadata selectByKey(String key);

    boolean add(ServiceMetadata serviceMetadata);

    boolean delete(String key);

    boolean exist(String key);

    Set<String> allServiceKeys();
}
