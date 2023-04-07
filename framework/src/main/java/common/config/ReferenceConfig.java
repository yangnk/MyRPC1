package common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import proxy.javassist.JavassistProxyFactory;
import registry.ServiceChangeListener;
import registry.ZookeeperClient;
import utils.ReferenceUtil;
import utils.SpringUtil;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class ReferenceConfig  implements InitializingBean, ApplicationContextAware, FactoryBean, Serializable {
    private transient ApplicationContext applicationContext;
    public String id;
    public String name;
    public String directServerIp;
    public int directServerPort;
    public String version;
    public long timeout;
    public long refCount;
    public transient List<ServiceConfig> services;
    public String ip;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        Class<?> clazz = getObjectType();
        // 动态代理，获取远程服务实例
        return JavassistProxyFactory.PROXY_FACTORY.getProxy(clazz, this);
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            log.error("没有对应的服务", e);
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationContext.containsBean("application")) {
            log.info("没有配置application，无法获取引用");
            return;
        }
        if (!applicationContext.containsBean("client")) {
            log.info("没有配置client，无法获取引用");
            return;
        }

        // 如果是点对点服务，不需要配置注册中心
        if (!StringUtils.isEmpty(directServerIp)) {
            log.info("点对点服务，" + directServerIp + ":" + directServerPort);
            return;
        }

        if (!applicationContext.containsBean("register")) {
            log.info("没有配置register，无法获取引用");
            return;
        }

        init();
    }

    public void init() throws Exception {
        // 发布客户端引用到注册中心
        registerReference();

        // 获取引用
        getReferences();

        // 缓存引用
        ReferenceUtil.put(this);

        // 订阅服务变化
        subscribeServiceChange();
    }

    private void subscribeServiceChange() {
        RegistryConfig register = (RegistryConfig) SpringUtil.getApplicationContext().getBean("register");
        String path = "/samples/" + name + "/provider";
        log.info("Start subscription service: [" + path + "]");
        // 订阅子目录变化
        ZookeeperClient.getInstance(register.getIp(), register.getPort()).subscribeChildChange(path, new ServiceChangeListener(name));

    }

    public void getReferences() throws Exception {
        String path = "/samples/" + name + "/provider";
        log.info("正在获取引用服务:[" + path + "]");
        RegistryConfig register = (RegistryConfig) SpringUtil.getApplicationContext().getBean("register");
        services = new ArrayList<>();
        ZookeeperClient zookeeperClient = ZookeeperClient.getInstance(register.getIp(), register.getPort());
        List<String> nodes = zookeeperClient.getChildNodes(path);

        for (String node : nodes) {
            ServiceConfig service = (ServiceConfig) zookeeperClient.getNode(path + "/" + node);
            // 版本为空，则可以匹配任意版本，都在必须匹配一致的版本
            if (!StringUtils.isEmpty(version) && !version.equals(service.getVersion())) {
                continue;
            }
            services.add(service);
        }
        log.info("引用服务获取完成[" + path + "]:" + services);
    }

    private void registerReference() throws UnknownHostException {
        RegistryConfig register = (RegistryConfig) SpringUtil.getApplicationContext().getBean("register");
        ip = InetAddress.getLocalHost().getHostAddress();

        // zookeeper
        String basePath = "/samples/" + this.getName() + "/consumer";
        String path = basePath + "/" + ip;

        ZookeeperClient client = ZookeeperClient.getInstance(register.getIp(), register.getPort());

        // 应用（路径）永久保存
        client.createPath(basePath);

        // 服务(数据)不永久保存，当与zookeeper断开连接20s左右自动删除
        client.saveNode(path, this);
        log.info("客户端引用发布成功:[" + path + "]");
    }

}
