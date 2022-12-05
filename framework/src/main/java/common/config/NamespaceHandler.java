package common.config;

import common.SimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("application", new SimpleBeanDefinitionParser(ApplicationConfig.class));
        registerBeanDefinitionParser("server", new SimpleBeanDefinitionParser(ServerConfig.class));
        registerBeanDefinitionParser("registry", new SimpleBeanDefinitionParser(RegistryConfig.class));
        registerBeanDefinitionParser("service", new SimpleBeanDefinitionParser(ServiceConfig.class));
        registerBeanDefinitionParser("client", new SimpleBeanDefinitionParser(ClientConfig.class));
        registerBeanDefinitionParser("reference", new SimpleBeanDefinitionParser(ReferenceConfig.class));
    }
}
