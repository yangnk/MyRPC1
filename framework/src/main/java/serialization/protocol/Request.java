package serialization.protocol;

import common.config.ServiceConfig;
import lombok.Data;
import java.io.Serializable;

@Data
public class Request implements Serializable {
    private String requestId;//请求id
    private String className;//类名
    private String methodName;//方法名
    private String[] type;//参数类型
    private Object[] args;//参数值
    private String clientApplicationName;//客户端应用名
    private String clientIp;//客户端ip
    private ServiceConfig serviceConfig;//服务配置
}
