package serialization.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Encoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public Encoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(msg)) {
            byte[] bytes = ProtostuffSerialization.enSerialize(msg);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            log.info("=== msg encoder success, msg is: {} ===", msg.toString());
        }
    }
}
