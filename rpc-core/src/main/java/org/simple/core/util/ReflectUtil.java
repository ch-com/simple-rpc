package org.simple.core.util;

import sun.rmi.transport.ObjectTable;

/**
 * @author ch
 * @date 2023/4/18
 */
public abstract class ReflectUtil {
    /**
     * 比较对象实例与类型是否匹配
     * @param objects 对象实例数组
     * @param objectType 对象类型数组
     * @return
     */
    public static boolean compareObjectType(Object[] objects, Class<?>[] objectType) {
        if (objects == null && objectType == null) {
            return true;
        }
        if ((objects == null && objectType != null) || (objects != null && objectType == null)) {
            return false;
        }
        if (objects.length != objectType.length) {
            return false;
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                continue;
            }
            if (!(objectType[i].isInstance(objects[i]))) {
                return false;
            }
        }
        return true;
    }
}
