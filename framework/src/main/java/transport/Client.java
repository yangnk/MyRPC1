package transport;

import balance.LoadBalance;
import common.config.ClientConfig;
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
import org.springframework.util.StringUtils;
import protocol.entity.Request;
import protocol.entity.Response;
import utils.SpringUtil;
import java.util.concurrent.TimeUnit;
import protocol.codec.Decoder;
import protocol.codec.Encoder;

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
                        ch.pipeline().addLast(clientHandler);
                        ch.pipeline().addLast(new RpcReadTimeoutHandler(clientHandler, referenceConfig.getTimeout(), TimeUnit.MILLISECONDS));
                    }
                });

        try {
            //如果服务是直连模式，则直接通过ip:port连接
            if (!StringUtils.isEmpty(referenceConfig.getDirectServerIp())) {
                channelFuture = bootstrap.connect(referenceConfig.getDirectServerIp(), referenceConfig.getDirectServerPort()).sync();
                log.info("successfully connected");
            } else {
                //如果服务是非直连模式，需要先从注册中心获取到服务实例，在根据服务实例中的i:port连接服务
                ClientConfig client = (ClientConfig) SpringUtil.getApplicationContext().getBean("client");
                log.info("the load balancing strategy is: " + client.getLoadBalance());

                ServiceConfig serviceConfig = LoadBalance.getService(referenceConfig, client.getLoadBalance());

                if (serviceConfig == null) {
                    return null;
                }
                channelFuture = bootstrap.connect(serviceConfig.getIp(), serviceConfig.getPort()).sync();
                log.info("successfully connected to the server: " + serviceConfig.getIp() + ":" + serviceConfig.getPort());
                return serviceConfig;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 客户端client进行远程调用
     * @param request
     * @return
     */
//    public Response remoteCall(Request request) {
//        try {
//            //发送请求
//            channelFuture.channel().writeAndFlush(request).sync();
//            channelFuture.channel().closeFuture().sync();
//
//            //接收响应
//            Response response = clientHandler.getResponse();
//            log.info("=== receive response from server: {} ===", response.toString());
//
//            return response;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public Response remoteCall(Request request) throws Throwable {

        // 发送请求
        channelFuture.channel().writeAndFlush(request).sync();
        channelFuture.channel().closeFuture().sync();

        // 接收响应
        Response response = clientHandler.getResponse();
        log.info("=== receive response from server: {} ===", response.toString());

        if (response.getSuccess()) {
            return response;
        }

        throw response.getError();
    }
}
