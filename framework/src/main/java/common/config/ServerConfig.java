package common.config;

import org.springframework.beans.factory.InitializingBean;
import transport.Server;

public class ServerConfig implements InitializingBean {


    private String id;

    private Integer port;

    /**
     * 在spring实例化全部的bean之后执行
     */
    @Override
    public void afterPropertiesSet() {
        // 启动服务
        new Server(port).start();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
