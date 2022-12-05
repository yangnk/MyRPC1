package balance;

import common.config.ReferenceConfig;
import common.config.ServiceConfig;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalance {
    private LoadBalance() {
    }

    /**
     * 根据负载均衡策略获取服务
     * @param reference
     * @param loadBalance
     * @return
     * @throws Exception
     */
    public static ServiceConfig getService(ReferenceConfig reference, String loadBalance) throws Exception {
        List<ServiceConfig> services = reference.getServices();
        if (services.isEmpty()) {
            throw new RuntimeException("no service available");
        }

        long count = reference.getRefCount();
        count++;
        reference.setRefCount(count);

        if (LoadBalancePolicy.RANDOM.getName().equals(loadBalance)) {
            // 随机
            return random(services);
        }
        return null;
    }

    /**
     * 随机策略
     * @param services
     * @return
     */
    private static ServiceConfig random(List<ServiceConfig> services) {
        return services.get(ThreadLocalRandom.current().nextInt(services.size()));
    }
}
