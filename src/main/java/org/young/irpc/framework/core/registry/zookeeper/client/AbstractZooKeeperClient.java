package org.young.irpc.framework.core.registry.zookeeper.client;

import lombok.Data;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * @ClassName AbstractZooKeeperClient
 * @Description 这里的client不是RPC的Server和Client
 * 而是Zookeeper的Client，利用客户端与Zookeeper交互
 * @Author young
 * @Date 2023/2/17 下午3:37
 * @Version 1.0
 **/
@Data
public abstract class AbstractZooKeeperClient {

    private String zkAddress;

    protected static int baseSleepTimes = 1000;

    protected static int maxRetryTimes = 3;


    public AbstractZooKeeperClient(String zkAddress){
        this.zkAddress = zkAddress;
    }

    /**
     * 更新某路径下节点的数据
     * @param address
     * @param data
     */
    public abstract void updateNodeData(String address, String data);

    /**
     * 获取客户端对象
     * @return
     */
    public abstract Object getClient();

    public abstract String getNodeData(String path);

    public abstract List<String> getChildrenData(String path);

    public abstract void createPersistentData(String address, String data);

    public abstract void createPersistentSeqData(String address, String data);

    public abstract void createTemporarySeqData(String address, String data);

    public abstract void createTemporaryData(String address, String data);

    public abstract void setTemporaryData(String address,String data);

    public abstract void destroy();

    public abstract List<String> listNode(String address);

    public abstract boolean clearNode(String address);

    public abstract boolean existNode(String address);

    /**
     * 监听path路径下某个节点变化
     * @param path
     * @param watcher
     */
    public abstract void watchNodeData(String path, Watcher watcher);

    /**
     * 监听子结点变化
     * @param path
     * @param watcher
     */
    public abstract void watchChildNodeData(String path, Watcher watcher);
}
