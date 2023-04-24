package org.simple.core.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.simple.core.domain.exchange.Message;
import org.simple.core.domain.exchange.RequestBody;
import org.simple.core.domain.exchange.ResponseBody;
import org.simple.core.exception.ClientParamException;
import org.simple.core.protocol.MessageSerializer;

/**
 * 处理消息编码
 * @author ch
 * @date 2023/4/19
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    // 报文长度字段占用长度
    public static final int LENGTH_SIZE = 4;
    // 报文长度字段偏移量，暂定从首字节开始
    public static final int LENGTH_OFFSET = 0;
    public static final int MES_TYPE = 1;
    public static final int SERIAL_TYPE = 1;
    public static final int HEADER_LENGTH = MES_TYPE + SERIAL_TYPE;


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        MessageSerializer serializer = Message.SeriaType.findSerializer(message
                .getSeriaTypeEnum().getCode());
        if (serializer == null) {
            throw new ClientParamException("Serialize type Not Supported");
        }
        byte[] bodyBytes = null;
        if (Message.Type.CALL.getCode() == message.getMesType().getCode()) {
            bodyBytes = serializer.encode((RequestBody) message.getData(), RequestBody.class);
        } else if (Message.Type.RESPONSE.getCode() == message.getMesType().getCode()) {
            bodyBytes = serializer.encode((ResponseBody) message.getData(), ResponseBody.class);
        }
        byteBuf.writeInt(bodyBytes.length + HEADER_LENGTH);// 写入报文总长度
        byteBuf.writeByte(message.getMesType().getCode());
        byteBuf.writeByte(message.getSeriaTypeEnum().getCode());
        byteBuf.writeBytes(bodyBytes);
    }
}
