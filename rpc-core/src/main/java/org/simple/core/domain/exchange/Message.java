package org.simple.core.domain.exchange;

import org.simple.core.protocol.MessageSerializer;
import org.simple.core.protocol.ProtoSerializer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * rpc传输消息结构
 * @author ch
 * @date 2023/4/18
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息首部 -> 消息类型
     */
    private Type mesType;
    /**
     * 消息首部 -> 主体序列化类型
     */
    private SeriaType seriaTypeEnum;

    /**
     * 消息主体
     */
    private MessageBody data;

    public SeriaType getSeriaTypeEnum() {
        return this.seriaTypeEnum;
    }

    public void setSeriaTypeEnum(SeriaType seriaTypeEnum) {
        this.seriaTypeEnum = seriaTypeEnum;
    }

    public Type getMesType() {
        return mesType;
    }

    public void setMesType(Type mesType) {
        this.mesType = mesType;
    }

    public MessageBody getData() {
        return data;
    }

    public void setData(MessageBody data) {
        this.data = data;
    }

    public static Message createMessage(Type mesType, SeriaType seriaTypeEnum, MessageBody body) {
        Message message = new Message();
        message.setMesType(mesType);
        message.setSeriaTypeEnum(seriaTypeEnum);
        message.setData(body);
        return message;
    }

    public static enum Type {
        CALL((byte)1),
        RESPONSE((byte)2)
        ;
        private byte code;
        Type(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return code;
        }
    }

    public static enum SeriaType {
        PROTOSTUFF((byte)1, new ProtoSerializer())
        ;
        private byte code;
        private MessageSerializer serializer;
        SeriaType(byte code, MessageSerializer serializer) {
            this.code = code;
            this.serializer = serializer;
        }

        public static MessageSerializer findSerializer(byte code) {
            return Arrays.stream(SeriaType.values()).filter(i -> i.getCode() == code)
                    .findFirst().get().getSerializer();
        }

        public byte getCode() {
            return code;
        }

        public MessageSerializer getSerializer() {
            return serializer;
        }
    }

}
