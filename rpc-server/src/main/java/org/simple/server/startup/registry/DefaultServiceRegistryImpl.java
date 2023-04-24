package org.simple.server.startup.registry;

import org.simple.core.domain.service.ServiceRegistry;
import org.simple.core.domain.service.ServiceMetadata;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ch
 * @date 2023/4/17
 */
public class DefaultServiceRegistryImpl implements ServiceRegistry {

    private ConcurrentHashMap<String, ServiceMetadata> serviceMetadataMap;

    public DefaultServiceRegistryImpl(int initSize) {
        serviceMetadataMap = new ConcurrentHashMap<>(initSize);
    }

    @Override
    public ServiceMetadata selectByKey(String key) {
        return serviceMetadataMap.get(key);
    }

    @Override
    public boolean add(ServiceMetadata serviceMetadata) {
        ServiceMetadata value = serviceMetadataMap.putIfAbsent(serviceMetadata.getServiceKey(), serviceMetadata);
        if (value == null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String key) {
        if (serviceMetadataMap.remove(key) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean exist(String key) {
        return serviceMetadataMap.containsKey(key);
    }

    @Override
    public Set<String> allServiceKeys() {
        return serviceMetadataMap.keySet();
    }
}
