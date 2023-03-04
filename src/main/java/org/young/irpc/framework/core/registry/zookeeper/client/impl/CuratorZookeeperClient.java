package org.young.irpc.framework.core.registry.zookeeper.client.impl;

import com.sun.org.apache.xerces.internal.xs.StringList;
import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.young.irpc.framework.core.registry.zookeeper.client.AbstractZooKeeperClient;

import java.util.List;

/**
 * @ClassName CuratorZookeeperClient
 * @Description 使用Curator框架实现交互
 * @Author young
 * @Date 2023/2/17 下午8:30
 * @Version 1.0
 **/
@Data
public class CuratorZookeeperClient extends AbstractZooKeeperClient {

    /**
     * 交互客户端
     */
    public CuratorFramework client;

    public CuratorZookeeperClient(String zkAddress){
        super(zkAddress);
        RetryPolicy policy = new ExponentialBackoffRetry(baseSleepTimes,maxRetryTimes);
        if (this.client==null){
            client = CuratorFrameworkFactory.newClient(zkAddress,
                    policy);
            client.start();
        }
    }

    @Override
    public void updateNodeData(String address, String data) {
        try {
            client.setData()
                    .forPath(address,data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getClient() {
        return client;
    }

    @Override
    public String getNodeData(String path) {
        byte[] data;
        try {
            data = client.getData().forPath(path);
            if (data!=null){
                return new String(data);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getChildrenData(String path) {

        try {
            List<String> data = client.getChildren().forPath(path);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createPersistentData(String address, String data) {
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(address, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createPersistentSeqData(String address, String data) {
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(address,data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTemporarySeqData(String address, String data) {
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(address,data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTemporaryData(String address, String data) {
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(address,data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTemporaryData(String address, String data) {
        try {
            client.setData()
                    .forPath(address,data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        client.close();
    }

    @Override
    public List<String> listNode(String address) {
        try {
            return client.getChildren().forPath(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean clearNode(String address) {
        try {
            client.delete().forPath(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existNode(String address) {
        try {
            Stat stat = client.checkExists().forPath(address);
            return stat!=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void watchNodeData(String path, Watcher watcher) {
        try {
            client.getData()
                    .usingWatcher(watcher)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用服务，开展监听
     * @param path
     * @param watcher
     */
    @Override
    public void watchChildNodeData(String path, Watcher watcher) {
        try {
            client.getChildren()
                    .usingWatcher(watcher)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
