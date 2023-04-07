package proxy.cglib;

import common.config.ReferenceConfig;
import org.springframework.cglib.proxy.Enhancer;
import proxy.ProxyFactory;

public class CglibProxyFactory implements ProxyFactory {
//    public static final ProxyFactory CGLIB_PROXY_FACTORY = new CglibProxyFactory();
    public static final ProxyFactory PROXY_FACTORY = new CglibProxyFactory();

    @Override
    public Object getProxy(Class<?> clazz, ReferenceConfig referenceConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new InvokerMethodInterceptor(referenceConfig));
        return enhancer.create();
    }
}
