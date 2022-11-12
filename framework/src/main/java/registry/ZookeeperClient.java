package registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import serialization.JdkSerializer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZookeeperClient {

    /**
     * 单例
     */
    private static ZookeeperClient instance;

    private CuratorFramework zkClient;

    private ZookeeperClient(String ip, String port) {
        log.info("start to connect to the zk server：[" + ip + ":" + port + "]");
        zkClient = CuratorFrameworkFactory.newClient(ip + ":" + port, new RetryNTimes(10, 5000));
        zkClient.start();
        log.info("successfully connected to the zk server：[" + ip + ":" + port + "]");
    }

    public static ZookeeperClient getInstance(String ip, String port) {
        if (null == instance) {
            instance = new ZookeeperClient(ip, port);
        }
        return instance;
    }


    /**
     * 判断路径是否存在
     *
     * @param path
     */
    private boolean exists(String path) {
        boolean result = false;
        try {
            result = zkClient.checkExists().forPath(path) != null;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return result;
    }

    /**
     * 创建永久节点
     *
     * @param path 路径
     */
    public void createPath(String path) {
        if (!exists(path)) {
            String[] paths = path.substring(1).split("/");
            String temp = "";
            for (String dir : paths) {
                temp += "/" + dir;
                if (!exists(temp)) {
                    try {
                        zkClient.create().withMode(CreateMode.PERSISTENT).forPath(temp);
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                }
            }
        }
    }

    /**
     * 创建临时节点
     *
     * @param path 数据保存路径
     * @param data 数据
     */
    public void saveNode(String path, Object data) {
        try {
            if (exists(path)) {
                zkClient.setData().forPath(path, JdkSerializer.enserialize(data));

            } else {
                String[] paths = path.substring(1).split("/");
                String temp = "";
                for (String dir : paths) {
                    temp += "/" + dir;
                    if (!exists(temp)) {
                        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(temp);
                    }
                }
                zkClient.setData().forPath(path, JdkSerializer.enserialize(data));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取子节点
     *
     * @param path
     * @return
     */
    public List<String> getChildNodes(String path) throws Exception{
        if (!exists(path)) {
            return new ArrayList<>();
        }
        return zkClient.getChildren().forPath(path);
    }

    /**
     * 获取节点
     *
     * @param path
     * @return
     */
    public Object getNode(String path) {
        if (!exists(path)) {
            return null;
        }
        try {
            return JdkSerializer.deserialize(zkClient.getData().forPath(path));

        }catch (Exception e){
            return null;
        }
    }

    /**
     * 订阅服务变化
     *
     * @param path
     * @param listener
     */
    public void subscribeChildChange(String path, CuratorListener listener) {
        zkClient.getCuratorListenable().addListener(listener);
    }
}
