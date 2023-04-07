package protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class Decoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public Decoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if ( byteBuf.readableBytes() < 4) {
            return;
        }
         byteBuf.markReaderIndex();
        int dataLength =  byteBuf.readInt();
        if (dataLength < 0) {
             channelHandlerContext.close();
        }
        if ( byteBuf.readableBytes() < dataLength) {
             byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
         byteBuf.readBytes(data);

        Object obj = ProtostuffSerialization.deserialize(data, genericClass);
         out.add(obj);
        log.info("=== msg decode success, out msg is: {} ===", out.toString());
    }

}
