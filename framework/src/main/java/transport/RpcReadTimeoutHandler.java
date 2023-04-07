package transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import protocol.entity.Response;

import java.util.concurrent.TimeUnit;

public class RpcReadTimeoutHandler extends ReadTimeoutHandler {

    private ClientHandler handler;

    private long timeout;

    public RpcReadTimeoutHandler(ClientHandler handler, long timeout, TimeUnit milliseconds) {
        super(timeout, milliseconds);
        this.handler = handler;
        this.timeout = timeout;
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        Response rpcResponse = new Response();
        rpcResponse.setSuccess(false);
        rpcResponse.setError(new RuntimeException("call service timeout,timeout=" + timeout + "ms"));
        handler.setResponse(rpcResponse);
        super.readTimedOut(ctx);
    }
}
