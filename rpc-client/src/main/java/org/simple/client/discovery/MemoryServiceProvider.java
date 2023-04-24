package org.simple.client.discovery;

import org.simple.client.request.RpcRequest;
import org.simple.core.domain.service.RegisterServiceMetadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * @author ch
 * @date 2023/4/18
 */
public class MemoryServiceProvider implements ServiceProvider{
    private final HashMap<String, Set<RegisterServiceMetadata>> serviceList = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    @Override
    public Set<RegisterServiceMetadata> matchService(RpcRequest request) {
        this.validate(request);
        String key = request.getServiceInterface() + request.getVersion();
        long stamp = lock.tryOptimisticRead();
        Set<RegisterServiceMetadata> metadata = serviceList.get(key);
        if (!lock.validate(stamp)) {
            // 有修改采用悲观读
            stamp = lock.readLock();
            try {
                metadata = serviceList.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return metadata;
    }

    public boolean addService(RegisterServiceMetadata serviceMetadata) {
        if (!serviceMetadata.validate()) {
            throw new IllegalArgumentException("param validate fail");
        }
        String key = serviceMetadata.getServiceInterface() + serviceMetadata.getServiceVersion();
        long stamp = lock.writeLock();
        try {
            Set<RegisterServiceMetadata> serviceMetadataSet = serviceList.get(key);
            if (serviceMetadataSet == null) {
                serviceMetadataSet = new HashSet<>();
                serviceList.put(key, serviceMetadataSet);
            }
            serviceMetadataSet.add(serviceMetadata);
        } finally {
            lock.unlockWrite(stamp);
        }
        return true;
    }

    private void validate(RpcRequest request) {
        if (request.getServiceInterface() == null || request.getVersion() == null) {
            throw new IllegalArgumentException("serviceInterface or version is null");
        }
    }
}


