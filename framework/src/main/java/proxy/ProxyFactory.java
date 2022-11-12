package proxy;

import common.config.ReferenceConfig;

/**
 * 代理类的接口
 */
public interface ProxyFactory {
    Object getProxy(Class<?> clazz, ReferenceConfig referenceConfig);
}
