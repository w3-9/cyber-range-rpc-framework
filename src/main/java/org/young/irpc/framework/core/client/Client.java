package org.young.irpc.framework.core.client;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.client.thread.AsyncSendJob;
import org.young.irpc.framework.core.common.constant.TestConstant;
import org.young.irpc.framework.core.common.rpc.adapter.client.ClientHandler;
import org.young.irpc.framework.core.common.rpc.adapter.DelimeterDecoder;
import org.young.irpc.framework.core.common.rpc.adapter.RpcDecoder;
import org.young.irpc.framework.core.common.rpc.adapter.RpcEncoder;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.config.ClientConfig;
import org.young.irpc.framework.core.common.config.PropertiesBootstrap;
import org.young.irpc.framework.core.common.constant.RegistryConstant;
import org.young.irpc.framework.core.common.data.DataService;
import org.young.irpc.framework.core.common.util.NetworkUtil;
import org.young.irpc.framework.core.common.watcher.listener.IrpcListenerLoader;
import org.young.irpc.framework.core.filter.client.ClientFilterChain;
import org.young.irpc.framework.core.proxy.jdk.JdkProxyFactory;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.server.AbstractRegistry;
import org.young.irpc.framework.core.registry.zookeeper.server.impl.ZookeeperRegistry;
import org.young.irpc.framework.core.router.IRouter;
import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName Client
 * @Description TODO
 * @Author young
 * @Date 2023/1/20 下午3:11
 * @Version 1.0
 **/
@Slf4j
@Data
public class Client {

    /**
     * 只有一个组
     */
    public NioEventLoopGroup clientGroup = null;

    /**
     * 配置服务器信息
     */
    private ClientConfig config;

    private Bootstrap bootstrap;


    private AbstractRegistry abstractRegistry;

    private IrpcListenerLoader irpcListenerLoader;

    public Client(){

    }

    public Client(ClientConfig config) {
        this.config = config;
    }

    /**
     * 将原来的函数拆分
     * @return
     * @throws InterruptedException
     */
    public RpcReference initClienApplication() throws Exception {
        clientGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /**
                         * 相关思考同Server.java
                         * 一旦切换位置，则会有处理过程放在解码过程前！会导致报错
                         */
                        ch.pipeline().addLast(new DelimeterDecoder(config.getPacketMaxSize()));
                        ch.pipeline().addLast(new RpcEncoder());
                        ch.pipeline().addLast(new RpcDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        irpcListenerLoader = new IrpcListenerLoader();
        irpcListenerLoader.init();

        this.config = PropertiesBootstrap.loadClientConfigFromLocal();
        CommonClientCache.CLIENT_CONFIG = this.config;
        initClientConfig();
//        /**
//         * 获取异步连接，放到新的线程里去
//         * todo: 为什么要这么做？
//         * 因为并不能马上得到结果。
//         */
//        ChannelFuture future = bootstrap.connect(
//                this.config.getServerAddr(),this.config.getPort()
//        ).sync();
//        log.info("Client service start ============>");
//        /**
//         * 将异步连接放到新线程，开始自动执行
//         */
//        this.startClient(future);
//        /**
//         * 注入代理RpcReference
//         */
        RpcReference rpcReference = new RpcReference(new JdkProxyFactory());
        return rpcReference;
    }

    public void subscribeService(Class serviceBean){
        if (abstractRegistry == null){
            abstractRegistry = new ZookeeperRegistry(config.getRegisterAddr());
        }
        URL url = new URL();
        url.setApplicationName(config.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParam(URL.HOST_STRING,NetworkUtil.getIpAddress());
        Map<String,String> serviceInfoMapping
                = abstractRegistry.getServiceWeightMap(serviceBean.getName());
        CommonClientCache.URL_MAP
                .put(serviceBean.getName(), serviceInfoMapping);
        abstractRegistry.subscribe(url);
    }

    public void doConnectServer(){
        try {
            Thread.sleep(TestConstant.SLEEP_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (String providerServiceName : CommonClientCache.SUBSCRIBE_SERVICE_LIST){
            List<String> providerAddresses =
                    abstractRegistry.getProviderIps(providerServiceName);
            for (String address : providerAddresses){
                try {
                    ConnectionHandler.connect(providerServiceName,address);
                } catch (InterruptedException e) {
                    log.error("Connection failed...");
                    e.printStackTrace();
                }
            }
            URL url = new URL();
            url.setServiceName(providerServiceName);
            url.addParam(URL.SERVICE_PATH,providerServiceName 
                    +
                    RegistryConstant.SEPERATOR
                    +
                    RegistryConstant.PROVIDER);
            url.addParam(URL.PROVIDER_IPs, JSON.toJSONString(providerAddresses));
            abstractRegistry.doAfterSubscribe(url);
        }
    }

    /**
     * Cancel future
     * @param
     */
    public void startClient(){

        //public void startClient(ChannelFuture future){
        /**
         * 将future放到新线程的变量里
         */
        new Thread(new AsyncSendJob()).start();
    }

    private void initClientConfig() throws Exception {
        String selectorStrategy = config.getSelectorStrategy();
        CommonClientCache.ROUTER
                = IRouter.loadAndgetInstance(selectorStrategy);
        CommonClientCache.SERIALIZATION_FACTORY
                = SerializeFactory.loadAndgetInstance(config.getSerialization());
        CommonClientCache.CLIENT_FILTER_CHAIN = new ClientFilterChain();
        CommonClientCache.CLIENT_FILTER_CHAIN.AddFilters();
    }

    public static void main(String[] args) throws Throwable {
//        ClientConfig config = new ClientConfig();
//        config.setPort(9090);
//        config.setServerAddr("localhost");

//        Client client = new Client(config);
//        RpcReference rpcReference = client.startClient();

        Client client = new Client();
        RpcReference rpcReference = client.initClienApplication();

        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup(client.getConfig().getGroup());
        rpcReferenceWrapper.setToken("token1");
        rpcReferenceWrapper.setAsync(false);

        /**
         * 绑定代理关系？
         */
//        DataService dataService = rpcReference.get(DataService.class);
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        client.subscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
        /**
         * 模拟用户操作
         */
        for(int i=0;i<100;i++){

            //用于测试服务上线下线
//            Thread.sleep(2000);
//            System.out.println("test"+i);

            String result = dataService.sendData("test");
            System.out.println(result);
        }
    }
}
