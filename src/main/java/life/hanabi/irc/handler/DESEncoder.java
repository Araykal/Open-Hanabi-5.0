package life.hanabi.irc.handler;

import cn.qiriyou.IIiIIiiiIiii;
import life.hanabi.irc.utils.DESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
@IIiIIiiiIiii
public class DESEncoder extends MessageToMessageEncoder<ByteBuf> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byte[] encoded = DESUtil.encrypt(bytes);
        ByteBuf buf = Unpooled.wrappedBuffer(encoded);
        list.add(buf);
    }
}
