package org.young.irpc.framework.core.registry.zookeeper.server.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.constant.RegistryConstant;
import org.young.irpc.framework.core.common.data.DataService;
import org.young.irpc.framework.core.common.watcher.event.IrpcEvent;
import org.young.irpc.framework.core.common.watcher.data.URLChangeWrapper;
import org.young.irpc.framework.core.common.watcher.event.impl.IrpcDataChangeEvent;
import org.young.irpc.framework.core.common.watcher.event.impl.IrpcUpdateEvent;
import org.young.irpc.framework.core.common.watcher.listener.IrpcListenerLoader;
import org.young.irpc.framework.core.registry.RegisterService;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.client.AbstractZooKeeperClient;
import org.young.irpc.framework.core.registry.zookeeper.client.impl.CuratorZookeeperClient;
import org.young.irpc.framework.core.registry.zookeeper.server.AbstractRegistry;
import org.young.irpc.framework.core.registry.zookeeper.server.ProviderNodeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ZookeeperRegistry
 * @Description TODO
 * 注册时要参考Dubbo
 * 先定义一个rpc的根节点，接着是不同的服务名称
 * （例如:com.sise.data.UserService）作为二级节点，
 * 在二级节点下划分了provider和consumer节点。
 * provider下存放的数据以ip+端口的格式存储。
 * consumer下边存放具体的服务调用服务名与地址
 * @Author young
 * @Date 2023/2/18 上午9:53
 * @Version 1.0
 **/
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry implements RegisterService {

    /**
     * 注册等一系列服务都是通过这个client完成
     */
    private AbstractZooKeeperClient zooKeeperClient;


    /**
     * 基于URL获取服务者路径
     * @param url
     * @return
     */
    private String getProviderPath(URL url){
        return RegistryConstant.ROOT
                + RegistryConstant.SEPERATOR
                + url.getServiceName()
                + RegistryConstant.SEPERATOR
                + RegistryConstant.PROVIDER
                + RegistryConstant.SEPERATOR
                + URL.getProviderAddress(url);
    }

    private String getConsumerPath(URL url){
        return RegistryConstant.ROOT
                + RegistryConstant.SEPERATOR
                + url.getServiceName()
                + RegistryConstant.SEPERATOR
                + RegistryConstant.CONSUMER
                + RegistryConstant.SEPERATOR
                + URL.getConsumerAddress(url);
    }

    /**
     * 传入RegistryAddr以实现客户端创建，封装客户端对外提供服务
     * @param address
     */
    public ZookeeperRegistry(String address){
        this.zooKeeperClient = new CuratorZookeeperClient(address);
    }

    public ZookeeperRegistry(){
        String address =
                CommonClientCache.CLIENT_CONFIG != null ?
                        CommonClientCache.CLIENT_CONFIG.getRegisterAddr() :
                        CommonServiceCache.SERVER_CONFIG.getRegisterAddr();
        this.zooKeeperClient = new CuratorZookeeperClient(address);
    }

    /**
     * 注册后开始监听服务路径
     * @param url
     */
    @Override
    public void doAfterSubscribe(URL url) {
        String newServerNodePath = RegistryConstant.ROOT
                + RegistryConstant.SEPERATOR
                + url.getServiceName()
                + RegistryConstant.SEPERATOR
                + RegistryConstant.PROVIDER;
        watchChildNodeData(newServerNodePath);
        /**
         * 不包含weight信息的为Key
         * 包含weight信息的为data
         */
        List<String> providerAddressList
                = JSON.parseObject(url.getParamMap()
                .get(URL.PROVIDER_IPs),List.class);
        for (String address : providerAddressList){
            watchNodeDataChange(
                    newServerNodePath
                    + RegistryConstant.SEPERATOR
                    + address
            );
        }
    }

    private void watchNodeDataChange(String path){
        zooKeeperClient.watchNodeData(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                String curPath = watchedEvent.getPath();
                String nodeData =
                        zooKeeperClient.getNodeData(curPath);
                nodeData = nodeData.replace(URL.SEPERATOR,
                        URL.SLASH);
                ProviderNodeInfo nodeInfo
                        =
                        URL.buildProviderFromString(nodeData);
                IrpcEvent irpcEvent = new IrpcDataChangeEvent(nodeInfo);
                IrpcListenerLoader.sendEvent(irpcEvent);
                watchNodeDataChange(path);
            }
        });
    }

    private void watchChildNodeData(String address){
        /**
         * 调用时，自定义监听方法和回调函数事件
         */
        zooKeeperClient.watchChildNodeData(address, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                log.info("watching for " + watchedEvent.toString());
                String path = watchedEvent.getPath();
                /**
                 * 收到通知以后刷新
                 */
                List<String> childrenDataList
                        = zooKeeperClient.getChildrenData(path);
                /**
                 * 将Path下的子数据节点记下，放到URLChangeWrapper中，用于比较
                 */
                URLChangeWrapper changeWrapper = new URLChangeWrapper();
                changeWrapper.setProviderURL(childrenDataList);
                /**
                 * 不知道到底是哪一个？
                 */
                changeWrapper.setServiceName(path.split(RegistryConstant.SEPERATOR)[1]);
                IrpcEvent event = new IrpcUpdateEvent(changeWrapper);
                IrpcListenerLoader.sendEvent(event);
                watchChildNodeData(address);
            }
        });
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }

    /**
     * 从ZooKeeper中获取
     * @param serviceName
     * @return
     */
    @Override
    public List<String> getProviderIps(String serviceName) {
        return zooKeeperClient.getChildrenData(
                RegistryConstant.ROOT
                + RegistryConstant.SEPERATOR
                + serviceName
                + RegistryConstant.SEPERATOR
                + RegistryConstant.PROVIDER
        );
    }

    @Override
    public Map<String, String> getServiceWeightMap(String serviceName) {
        List<String> providerIps
                = this.getProviderIps(serviceName);
        Map<String,String> weightMap = new HashMap<>();
        for (String address : providerIps){
            String nodeData
                    = zooKeeperClient.getNodeData(
                    RegistryConstant.ROOT
                            + RegistryConstant.SEPERATOR
                            + serviceName
                            + RegistryConstant.SEPERATOR
                            + RegistryConstant.PROVIDER
                            + RegistryConstant.SEPERATOR
                            + address
            );
            weightMap.put(address,nodeData);
        }
        return weightMap;
    }


    @Override
    public void register(URL url) {

        if (!zooKeeperClient.existNode(RegistryConstant.ROOT)){
            zooKeeperClient.createPersistentData(RegistryConstant.ROOT,"");
        }

        /**
         * urlString is content
         */
        String urlString = URL.providerURL(url);
        String providerPath = getProviderPath(url);

        if (!zooKeeperClient.existNode(providerPath)){
            zooKeeperClient.createTemporaryData(providerPath,urlString);
        }else{
            zooKeeperClient.clearNode(providerPath);
            zooKeeperClient.createTemporaryData(providerPath,urlString);
        }

        super.register(url);
    }

    @Override
    public void unregister(URL url) {
        zooKeeperClient.clearNode(getProviderPath(url));
        super.unregister(url);
    }

    @Override
    public void subscribe(URL url) {

        if (!zooKeeperClient.existNode(RegistryConstant.ROOT)){
            zooKeeperClient.createPersistentData(RegistryConstant.ROOT,"");
        }

        String urlString = URL.consumerURL(url);
        String consumerPath =  getConsumerPath(url);

        /**
         * todo 为什么是创建 持久化？
         */
        if (!zooKeeperClient.existNode(consumerPath)){
            zooKeeperClient.createTemporarySeqData(consumerPath,urlString);
        }else{
            zooKeeperClient.clearNode(consumerPath);
            zooKeeperClient.createTemporarySeqData(consumerPath,urlString);
        }

        super.subscribe(url);
//        this.doAfterSubscribe(url);
    }

    @Override
    public void unsubscribe(URL url) {
        super.unsubscribe(url);
    }

    public static void main(String[] args) throws InterruptedException {
        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry("localhost:2181");
        List<String> urls = zookeeperRegistry.getProviderIps(
                DataService.class.getName()
        );
        System.out.println(urls);
        Thread.sleep(2000);
        System.out.println(zookeeperRegistry.getServiceWeightMap(
                DataService.class.getName()
        ));;
    }
}
