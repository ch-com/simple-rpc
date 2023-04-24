package org.simple.core.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.MessageBody;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.handler.outbound.MessageEncoder;
import org.simple.core.protocol.MessageSerializer;

/**
 * 处理消息解码
 * @author ch
 * @date 2023/4/19
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    /**
     *
     * @param maxFrameLength 最大报文长度
     * @param lengthFieldOffset 长度字段偏移量
     * @param lengthFieldLength 长度字段字节数
     */
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object message = super.decode(ctx, in);
        if (message instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) message;
            // 报文长度
            int allLength = buf.readInt();
            // 报文类型
            byte messageType = buf.readByte();
            // 序列化类型
            byte serialType = buf.readByte();
            // 消息体长度
            int bodyLength = allLength - MessageEncoder.HEADER_LENGTH;
            if (bodyLength > 0) {
                byte[] body = new byte[bodyLength];
                buf.readBytes(body);
                try {
                    MessageSerializer serializer = Message.SeriaType.findSerializer(serialType);
                    if (Message.Type.CALL.getCode() == messageType) {
                        MessageBody request = serializer.decode(body, RequestBody.class);
                        return request;
                    } else if (Message.Type.RESPONSE.getCode() == messageType) {
                        MessageBody request = serializer.decode(body, ResponseBody.class);
                        return request;
                    }
                } finally {
                    buf.release();
                }
            }
        }
        return message;
    }

}
