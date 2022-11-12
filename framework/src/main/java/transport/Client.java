package transport;

import common.config.ReferenceConfig;
import common.config.ServiceConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import serialization.protocol.Request;
import serialization.protocol.Response;
import serialization.codec.*;

@Slf4j
public class Client {
    private ReferenceConfig referenceConfig;
    private ChannelFuture channelFuture;
    private ClientHandler clientHandler;

    public Client(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    /**
     * 和服务端server建立连接
     * @return
     */
    public ServiceConfig connectServer() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Encoder(Request.class));
                        ch.pipeline().addLast(new Decoder(Response.class));

                        clientHandler = new ClientHandler();
//                        ch.pipeline().addLast(new RpcReadTimeoutHandler(clientHandler, referenceConfig.getTimeout(), TimeUnit.MILLISECONDS));//todo 可以加上超时处理器
                        ch.pipeline().addLast(clientHandler);
                    }
                });

        try {
            channelFuture = bootstrap.connect(referenceConfig.getDirectServerIp(), referenceConfig.getDirectServerPort()).sync();
            log.info("=== client connnect success. ===");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
        //todo 可以完善负载均衡策略
    }

    /**
     * 客户端client进行远程调用
     * @param request
     * @return
     */
    public Response remoteCall(Request request) {
        try {
            //发送请求
            channelFuture.channel().writeAndFlush(request).sync();
            channelFuture.channel().closeFuture().sync();

            //接收响应
            Response response = clientHandler.getResponse();
            log.info("=== receive response from server: {} ===", response.toString());

            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
