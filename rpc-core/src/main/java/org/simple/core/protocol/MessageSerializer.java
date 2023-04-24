package org.simple.core.protocol;

/**
 * 消息序列化器
 * @author ch
 * @date 2023/4/19
 */
public interface MessageSerializer {
    /**
     * 对象编码
     * @param obj
     * @return
     */
    <T> byte[] encode(T obj, Class<T> sClass);

    /**
     * 解码
     * @param bytes
     * @param tClass
     * @return
     */
    <T> T decode(byte[] bytes, Class<T> tClass);
}
