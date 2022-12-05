package common.config;

import common.RpcContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import utils.SpringUtil;

import java.net.InetAddress;

public class ApplicationConfig implements ApplicationContextAware, InitializingBean {

    private String id;

    private String name;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.setApplicationContext(applicationContext);
    }

    /**
     * 在spring实例化全部的bean之后执行
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // 上下文环境
        RpcContext.setApplicationName(name);
        RpcContext.setLocalIp(InetAddress.getLocalHost().getHostAddress());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
