package org.simple.core.protocol;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * ProtoStuff序列化实现
 * @author ch
 * @date 2023/4/19
 */
public class ProtoSerializer implements MessageSerializer {

    @Override
    public <T> byte[] encode(T obj, Class<T> sClass) {
        Schema schema = RuntimeSchema.getSchema(sClass);
        byte[] bytes = null;
        bytes = ProtostuffIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate());
        return bytes;
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> aClass) {
        Schema<T> schema = RuntimeSchema.getSchema(aClass);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
