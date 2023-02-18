package life.hanabi.irc;

import cn.qiriyou.IIiIIiiiIiii;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import life.hanabi.irc.handler.DelimiterEncoder;
import life.hanabi.utils.PlayerUtil;
import org.apache.commons.io.Charsets;
@IIiIIiiiIiii
public class IRCClient {

    public static String username;
    public static String password;

    public static String address = "101.43.166.241";

    public int port = 5557;
    public int flag = 0;

    public Bootstrap bootstrap;

    public void connect() {
        System.out.println("tried to login");
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        String delimiter = "_$_";
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(delimiter.getBytes())));
                nioSocketChannel.pipeline().addLast("decoder", new StringDecoder(Charsets.UTF_8));
                nioSocketChannel.pipeline().addLast("encoder", new StringEncoder(Charsets.UTF_8));
                nioSocketChannel.pipeline().addLast(new DelimiterEncoder(delimiter));
                nioSocketChannel.pipeline().addLast(new ClientHandler());
            }
        });
        try {
            ChannelFuture cf = bootstrap.connect(address, port).sync();
            cf.addListener(future -> {
                if (future.cause() != null) {
                    PlayerUtil.tellPlayerWithPrefix("IRC failed to reconnect");
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ChannelFuture reconnect() {
        try {
            return bootstrap.connect(address, port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

