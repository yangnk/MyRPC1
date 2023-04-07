package common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import registry.ZookeeperClient;
import utils.SpringUtil;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

//@Data
@Slf4j
public class ServiceConfig implements InitializingBean, ApplicationContextAware, Serializable{
    private transient ApplicationContext applicationContext;
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
        if (!applicationContext.containsBean("server")) {
            log.info("没有配置server，不发布到注册中心");
            return;
        }
        if (!applicationContext.containsBean("register")) {
            log.info("没有配置register，不发布到注册中心");
            return;
        }

        // 发起服务注册
        registerService();
    }

    /**
     * 发起服务注册
     */
    private void registerService() throws UnknownHostException {
        RegistryConfig register = (RegistryConfig) SpringUtil.getApplicationContext().getBean("register");
        ServerConfig server = (ServerConfig) applicationContext.getBean("server");

        this.setPort(server.getPort());

        // zookeeper
        String basePath = "/samples/" + this.getName() + "/provider";
        String path = basePath + "/" + InetAddress.getLocalHost().getHostAddress() + "_" + port;
        ZookeeperClient client = ZookeeperClient.getInstance(register.getIp(), register.getPort());
        client.createPath(basePath);
        this.setIp(InetAddress.getLocalHost().getHostAddress());
//        client.saveNode(path, "1111111111");
        client.saveNode(path, this);
        log.info("service published successfully: [" + path + "]");
    }


    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", impl='" + impl + '\'' +
                ", ref='" + ref + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", version=" + version +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
