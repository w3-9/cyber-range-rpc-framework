package org.young.irpc.framework.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.constant.AnnotationConstant;
import org.young.irpc.framework.core.common.rpc.adapter.DelimeterDecoder;
import org.young.irpc.framework.core.common.rpc.adapter.RpcDecoder;
import org.young.irpc.framework.core.common.rpc.adapter.RpcEncoder;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.config.PropertiesBootstrap;
import org.young.irpc.framework.core.common.config.ServerConfig;
import org.young.irpc.framework.core.common.constant.TestConstant;
import org.young.irpc.framework.core.common.data.impl.DataServiceImpl;
import org.young.irpc.framework.core.common.rpc.adapter.server.MaxConnectionLimitHandler;
import org.young.irpc.framework.core.common.rpc.adapter.server.ServerHandler;
import org.young.irpc.framework.core.common.util.NetworkUtil;
import org.young.irpc.framework.core.filter.server.ServerFilterChain;
import org.young.irpc.framework.core.registry.RegisterService;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.server.AbstractRegistry;
import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.util.concurrent.Semaphore;

import static org.young.irpc.framework.core.common.cache.CommonServiceCache.PROVIDER_SERVICE_MAP;

/**
 * @ClassName Server
 * @Description 服务端 （似乎没有用户的参与，直接在stub层进行）
 * @Author young
 * @Date 2023/1/17 下午7:32
 * @Version 1.0
 **/
@Data
@Slf4j
public class Server {

    /**
     * 本来写了static，但隐去了，因为不知道
     * static具体有什么用
     * bossGroup是主Reactor
     */
    private EventLoopGroup bossGroup = null;

    /**
     * workerGroup是分Reactor
     */
    private EventLoopGroup workerGroup = null;

    /**
     * 对Server的配置，应当允许从配置项中修改
     */
    private ServerConfig serverConfig;


    /**
     * 注册/订阅服务，这里主要是注册用
     */
    private RegisterService registerService;

    /**
     * 启动应用 这里封装了netty的一系列操作
     */
    public void startApplication() throws InterruptedException {

        /**
         * netty的实例化，需要启动两个EventLoopGroup，事件循环组 一个boos，一个worker
         * 大部分场景中，我们使用的主从多线程Reactor模型，
         * Boss线程是主Reactor，Worker是从Reactor
         * EventLoopGroup可以理解为线程池
         */
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        /**
         * bootstrap里面都封装好了NIO的代码模板
         * group(bossGroup,workerGroup)指定从属关系
         * childHandler指定处理器并初始化通道
         */
        bootstrap.group(bossGroup,workerGroup)
                // 选择NioChannel类型
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new MaxConnectionLimitHandler(serverConfig.getMaxTCPConnection()))
                /**
                 * 当新的客户端连接的时候，才会执行 initChannel
                 * 添加了RpcEncoder、Decoder和Handler
                 * todo 这个添加的顺序会有影响吗？？
                 * 因为有inbound&outbound的区别，所以会按照顺序来
                 */
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加处理器
                        log.info("initializing provider");
                        ch.pipeline().addLast(new DelimeterDecoder(serverConfig.getPacketMaxSize()));
                        ch.pipeline().addLast(new RpcEncoder());
                        ch.pipeline().addLast(new RpcDecoder());
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });

        this.batchExportUrl();
        CommonServiceCache.SERVER_DISPATCHER.consume();
        bootstrap.bind(this.serverConfig.getPort())
                /**
                 * bind的时候已经开始监听流程了，sync是为了让主线程监听流程处理完毕，因此会主动阻塞
                 */
                .sync();
    }

    /**
     * Server会将本地的服务进行预注册，传入的是Object（Service实现类）
     * 预注册是指不是直接注册，是存好，由batchExport批量注册
     * @param serviceBean 待注册的服务Bean + 元数据所包装
     */
    private URL registerService(Object serviceBean) throws Exception {
        /**
         * getInterfaces()返回的是接口列表（数组）
         * 通过反射获取，尝试拿到里面的接口类
         * 在测试过程中，我们使用实现DataService的DataServiceImpl
         */
        if (serviceBean.getClass().getInterfaces().length == 0){
            throw new RuntimeException("Service must have interfaces");
        }
        Class<?>[] classes = serviceBean.getClass().getInterfaces();

        /**
         * 不知道为什么只能有一个接口(不是方法)
         * 因为到时候不知道究竟实现的是哪个接口，会有一点乱
         */
        if (classes.length > 1){
            throw new RuntimeException("Service must have only 1 interface");
        }

        /**
         * 使用ZooKeeperRegistry注册 => SPI
         */
        if (registerService == null){
//            registerService = new ZookeeperRegistry(serverConfig.getRegisterAddr());
            registerService = AbstractRegistry.loadAndgetInstance(serverConfig.getRegisterType());
        }

        Class interfaceInfo = classes[0];
        /**
         * 使用内存中的MAP去维护interface名称到实现类的映射
         */
        PROVIDER_SERVICE_MAP.put(interfaceInfo.getName(),serviceBean);
        log.info("service "+ interfaceInfo.getName()+" registered");

        URL url = new URL();
        url.setServiceName(interfaceInfo.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        url.addParam(URL.HOST_STRING, NetworkUtil.getIpAddress());
        url.addParam(URL.PORT_STRING,String.valueOf(serverConfig.getPort()));
        return url;


    }

    public void registerService(ServerWrapper serverWrapper) throws Exception {
        URL url = this.registerService(serverWrapper.getServiceObj());


        url.addParam(URL.GROUP_STRING,serverWrapper.getGroup());
        url.addParam(URL.LIMIT_STRING,String.valueOf(serverWrapper.getFlowLimit()));
        /**
         * 将服务提供商信息存放到Provider_URL_set里，等待后续注册
         */
        CommonServiceCache.PROVIDER_URL_SET.add(url);
        CommonServiceCache.PROVIDER_SERVER_WRAPPER
                .put(url.getServiceName(),
                        serverWrapper);
        CommonServiceCache.SERVER_FLOW_LIMIT_SEMAPHORE.put(url.getServiceName(),new Semaphore(serverWrapper.getFlowLimit()));
    }

    public void initServerConfig() throws Exception {
        ServerConfig config = PropertiesBootstrap.loadServerConfigFromLocal();
        this.serverConfig = config;
        CommonServiceCache.SERVER_CONFIG = config;

        CommonServiceCache.SERIALIZATION_FACTORY
                = SerializeFactory.loadAndgetInstance(serverConfig.getSerialization());


        CommonServiceCache.SERVER_FILTER_CHAIN_BEFORE = new ServerFilterChain(AnnotationConstant.SPI_BEFORE);

        CommonServiceCache.SERVER_FILTER_CHAIN_AFTER = new ServerFilterChain(AnnotationConstant.SPI_AFTER);


        CommonServiceCache.SERVER_DISPATCHER.init(config.getBlockQueueSize(),
                config.getBizThreadsNum());

        log.info("serverconfig finished...");
    }


    /**
     * 起多线程，将PROVIDER_URL_SET（自身对外服务）注册到Zookeeper中
     */
    public void batchExportUrl(){
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    /**
//                     * 服务延迟，等主线程加入完成
//                     */
//                    Thread.sleep(TestConstant.SLEEP_TIME);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                /**
                 * 将相关服务都注册
                 */
                for (URL url : CommonServiceCache.PROVIDER_URL_SET){
                    log.warn("logging service "+ url.getServiceName());
                    registerService.register(url);
                }
            }
        });
        task.start();
    }

    /**
     * 测试用途
     * @param args
     */
    public static void main(String[] args) throws Exception {

        /**
         * 创建Server选项
         * 这是先前设计，已经注释了
         */
//        ServerConfig config = new ServerConfig();
//        config.setPort(9090);

        /**
         * 启动服务
         */
        Server server = new Server();
//        server.setServerConfig(config);
        /**
         * 从Property文件中加载配置信息
         */
        server.initServerConfig();
        /**
         * 将服务注册到Zookeeper上
         */
        ServerWrapper wrapper =
                new ServerWrapper(
                    new DataServiceImpl(),
                    server.getServerConfig()
                        .getGroup()
            );
        wrapper.setToken("token1");
        wrapper.setFlowLimit(server.serverConfig.getFlowLimit());


        server.registerService(wrapper);
        server.startApplication();


    }
}
