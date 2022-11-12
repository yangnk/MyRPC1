package common;

import lombok.Data;
import java.util.UUID;

@Data
public class RpcContext {
    private static ThreadLocal<String> uuid = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
    private static String applicationName;
    private static String localIp;

    public static ThreadLocal<String> getUuid() {
        return uuid;
    }

    public static void setUuid(ThreadLocal<String> uuid) {
        RpcContext.uuid = uuid;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        RpcContext.applicationName = applicationName;
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        RpcContext.localIp = localIp;
    }
}
