package proxy.cglib;

import common.config.ReferenceConfig;
import common.RpcContext;
import common.config.ServiceConfig;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import serialization.protocol.Request;
import serialization.protocol.Response;
import transport.Client;
import java.lang.reflect.Method;

public class InvokerMethodInterceptor implements MethodInterceptor {
    private ReferenceConfig referenceConfig;

    public InvokerMethodInterceptor(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return remoteCall(referenceConfig, method.getName(), method.getParameterTypes(), objects);
    }

    private Object remoteCall(ReferenceConfig referenceConfig, String methodName, Class<?>[] parameterTypes, Object[] objects) {
        //组装Request
        Request request = new Request();
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());
        request.setClassName(referenceConfig.getName());
        request.setMethodName(methodName);
        request.setType(getTypes(parameterTypes));
        request.setArgs(objects);

        //连接RPC server
        Client client = new Client(referenceConfig);
        ServiceConfig serviceConfig = client.connectServer();
        request.setServiceConfig(serviceConfig);

        //进行远程调用
        Response response = client.remoteCall(request);
        Object result = response.getResult();
        return result;
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
