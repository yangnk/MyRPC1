package proxy.javassist;

import common.RpcContext;
import common.config.ReferenceConfig;
import common.config.ServiceConfig;
import serialization.protocol.Request;
import serialization.protocol.Response;
import transport.Client;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler implements InvocationHandler {
    ReferenceConfig referenceConfig = new ReferenceConfig();
    public InvokerInvocationHandler(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(method.getName(), method.getParameterTypes(), args);
    }

    public Object invoke(String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 同步调用
        return remoteCall(referenceConfig, methodName, argTypes, args);
    }

    private Object remoteCall(ReferenceConfig refrence, String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 准备请求参数
        Request request = new Request();
        // 请求id
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());
        // 必要参数
        request.setClassName(referenceConfig.getName());
        request.setMethodName(methodName);
        request.setType(getTypes(argTypes));
        request.setArgs(args);
        Response response;
        try {
            Client client = new Client(refrence);
            ServiceConfig service = client.connectServer();
            request.setServiceConfig(service);
            response = client.remoteCall(request);
            return response.getResult();
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 获取方法的参数类型
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
