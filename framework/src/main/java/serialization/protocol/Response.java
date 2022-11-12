package serialization.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private String requestId;//请求id
    private Boolean success;//是否成功
    private Object result;//返回结果
}
