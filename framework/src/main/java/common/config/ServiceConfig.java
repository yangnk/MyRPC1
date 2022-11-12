package common.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.io.Serializable;

@Data
public class ServiceConfig implements InitializingBean, ApplicationContextAware, Serializable {
    private ApplicationContext applicationContext;
    private String id;
    private String name;
    private String impl;
    private String ref;
    private String ip;
    private int port;
    private String version;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //todo
    }
}
