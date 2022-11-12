package proxy.javassist;

import common.config.ReferenceConfig;
import proxy.ProxyFactory;
import proxy.cglib.CglibProxyFactory;

public class JavassistProxyFactory implements ProxyFactory {
    public static final ProxyFactory JAVASSIST_PROXY_FACTORY = new CglibProxyFactory();

    @Override
    public Object getProxy(Class<?> clazz, ReferenceConfig referenceConfig) {
        try {
            return Proxy.getProxy(clazz).newInstance(new InvokerInvocationHandler(referenceConfig));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
