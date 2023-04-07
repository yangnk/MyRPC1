package proxy.cglib;

import common.config.ReferenceConfig;
import common.RpcContext;
import common.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import protocol.entity.Request;
import protocol.entity.Response;
import transport.Client;
import java.lang.reflect.Method;

@Slf4j
public class InvokerMethodInterceptor implements MethodInterceptor {
    private ReferenceConfig referenceConfig;

    public InvokerMethodInterceptor(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return remoteCall(referenceConfig, method.getName(), method.getParameterTypes(), objects);
    }

    private Object remoteCall(ReferenceConfig referenceConfig, String methodName, Class<?>[] parameterTypes, Object[] objects) throws Throwable {
        //组装Request
        Request request = new Request();
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());
        request.setClassName(referenceConfig.getName());
        request.setMethodName(methodName);
        request.setType(getTypes(parameterTypes));
        request.setArgs(objects);

        Response response;
        try {
            Client client = new Client(referenceConfig);
            ServiceConfig service = client.connectServer();
            request.setServiceConfig(service);
            response = client.remoteCall(request);
            return response.getResult();
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     * 方法参数类型类转为参数类型名
     * @param methodTypes
     * @return
     */
    private String[] getTypes(Class<?>[] methodTypes) {
        String[] types = new String[methodTypes.length];
        for (int i = 0; i < methodTypes.length; i++) {
            types[i] = methodTypes[i].getName();
        }
        return types;
    }
}
