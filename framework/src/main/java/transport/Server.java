package transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import protocol.codec.Decoder;
import protocol.codec.Encoder;
import protocol.entity.Request;
import protocol.entity.Response;


@Slf4j
public class Server extends Thread {
    private Integer port;

    public Server(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        //创建NioEventLoopGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        //创建ServerBootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            //ServerBootstrap注册关联channel，handler
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new Encoder(Request.class));
                            channel.pipeline().addLast(new Decoder(Response.class));
                            channel.pipeline().addLast(new ServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("=== server start success. listen port is: {}  ===", port);

            //关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
