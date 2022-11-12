package transport;

import utils.SpringUtil;
import utils.TypeParseUtil;
import common.config.ServiceConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import serialization.protocol.Request;
import serialization.protocol.Response;
import java.util.Map;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取Request
        Response response = new Response();
        Request request = (Request) msg;
        response.setRequestId(request.getRequestId());

        //获取保存服务（ServiceConfig）列表的IoC容器
        Map<String, ServiceConfig> serviceMap = SpringUtil.getApplicationContext().getBeansOfType(ServiceConfig.class);
        ServiceConfig service = null;
        try {
            //从IoC容器中查找对应服务
            for (String key : serviceMap.keySet()) {
                if (serviceMap.get(key).getName().equals(request.getClassName())) {
                    service = serviceMap.get(key);
                    break;
                }
            }

            //没有查到到对应服务
            if (service == null) {
                throw new RuntimeException("no service found: " + request.getClassName());
            }

            //获取服务实现类
            Object serviceImpl = SpringUtil.getApplicationContext().getBean(service.getRef());
            if (serviceImpl == null) {
                throw new RuntimeException("no available service found: " + request.getClassName());
            }

            // 转换参数和参数类型
            Map<String, Object> map = null;
            map = TypeParseUtil.parseTypeString2Class(request.getType(), request.getArgs());
            Class<?>[] classTypes = (Class<?>[]) map.get("classTypes");
            Object[] args = (Object[]) map.get("args");

            // 通过反射调用方法获取返回值
            Object result = serviceImpl.getClass().getMethod(request.getMethodName(), classTypes).invoke(serviceImpl, args);
            response.setResult(result);
            response.setSuccess(true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        ctx.write(response);
        ctx.flush();
    }
}
