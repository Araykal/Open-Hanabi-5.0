package life.hanabi.irc.client.test;

import life.hanabi.irc.handler.DelimiterEncoder;
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

public class ClientTest {

    public static void main(String[] args) {
        new ClientTest().connect();
    }

    public static Bootstrap bootstrap;

    public void connect() {
        System.out.println("HI");
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        String delimiter = "_$_";
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(delimiter.getBytes())));
                nioSocketChannel.pipeline().addLast("decoder", new StringDecoder());
                nioSocketChannel.pipeline().addLast("encoder", new StringEncoder());
                nioSocketChannel.pipeline().addLast(new DelimiterEncoder(delimiter));
                nioSocketChannel.pipeline().addLast(new Handler());
            }
        });
        try {
            ChannelFuture cf = bootstrap.connect("101.43.166.241", 5557).sync();
            cf.addListener(future -> {
                if(future.cause() != null){
                    System.out.println("Failed to reconnect");
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

