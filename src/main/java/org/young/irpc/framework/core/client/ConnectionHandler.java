package org.young.irpc.framework.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.util.ListUtil;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.server.ProviderNodeInfo;
import org.young.irpc.framework.core.router.Selector;

import java.util.*;

/**
 * @ClassName ConnectionHandler
 * @Description 连接控制器，连接池就在这里
 * @Author young
 * @Date 2023/2/18 下午1:38
 * @Version 1.0
 **/
@Slf4j
public class ConnectionHandler {

    private static final String SEPERATOR = ";";
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap){
        ConnectionHandler.bootstrap = bootstrap;
    }

    public static void connect(String providerServiceName,
                               String providerAddress) throws InterruptedException {
        if (bootstrap == null){
            throw  new RuntimeException("bootstrap not initlized...");
        }

        if (!providerAddress.contains(SEPERATOR)){
            log.error("Invalid Server Address");
            return;
        }

        String host = providerAddress.split(SEPERATOR)[0];
        int port = Integer.valueOf(providerAddress.split(SEPERATOR)[1]);

        ChannelFuture future
                = createChannelFuture(host,port);

        ChannelFutureWrapper wrapper
                = new ChannelFutureWrapper();
        wrapper.setPort(port);
        wrapper.setHost(host);
        wrapper.setChannelFuture(future);

        String providerURLINFO = CommonClientCache.URL_MAP
                        .get(providerServiceName)
                                .get(providerAddress);
        providerURLINFO = providerURLINFO.replace(
                URL.SEPERATOR,
                URL.SLASH
        );
        ProviderNodeInfo providerNodeInfo
                = URL.buildProviderFromString(providerURLINFO);

        wrapper.setWeight(providerNodeInfo.getWeight());
        wrapper.setGroup(providerNodeInfo.getGroup());

        CommonClientCache.SERVER_ADDRESS.add(providerAddress);

        List<ChannelFutureWrapper> wrappers
                = CommonClientCache.CONNECT_MAP
                .get(providerServiceName);
        if (ListUtil.isEmpty(wrappers)){
            wrappers = new ArrayList<>();
        }
        wrappers.add(wrapper);
        CommonClientCache.CONNECT_MAP.put(
                providerServiceName,
                wrappers
        );


        Selector selector = new Selector();
        selector.setProviderServiceName(providerServiceName);
        CommonClientCache.ROUTER.refreshRouterArray(selector);

    }

    public static ChannelFuture createChannelFuture(String ip, Integer port) throws InterruptedException {
        ChannelFuture future =
                bootstrap.connect(ip,port).sync();
        return future;
    }

    public static void disconnect(String providerServiceName,
                                  String providerAddress){
        CommonClientCache.SERVER_ADDRESS.remove(providerAddress);
        List<ChannelFutureWrapper> wrappers
                = CommonClientCache.CONNECT_MAP
                .get(providerServiceName);
        if (ListUtil.isNotEmpty(wrappers)){
            Iterator<ChannelFutureWrapper> iterator
                    = wrappers.iterator();
            while (iterator.hasNext()){
                ChannelFutureWrapper wrapper = iterator.next();
                if (providerAddress.equals(wrapper.getAddr())){
                    iterator.remove();
                }
            }
        }
    }


    public static ChannelFuture getChannelFuture(RpcInvocation invocation){
//        List<ChannelFutureWrapper> wrappers
//                = CommonClientCache.CONNECT_MAP
//                .get(providerServiceName);
//        if (ListUtil.isEmpty(wrappers)){
//            log.error("no provider available...");
//            throw new RuntimeException("no provider available");
//        }

        ChannelFutureWrapper[] wrappers
                = CommonClientCache.SERVER_ROUTER_MAP
                .get(invocation.getTargetServiceName());

        List<ChannelFutureWrapper> wrapperList
                = new ArrayList<>(Arrays.asList(wrappers));

        CommonClientCache.CLIENT_FILTER_CHAIN
                .doFilter(wrapperList,invocation);

        ChannelFutureWrapper[] wrappers1 = new ChannelFutureWrapper[wrapperList.size()];
        wrapperList.toArray(wrappers1);
        /**
         * 默认随机
         */
//        ChannelFuture future =
//                wrappers.get(new Random().nextInt(wrappers.size()))
//                        .getChannelFuture();
        Selector selector = new Selector();
        selector.setProviderServiceName(invocation.getTargetServiceName());
        selector.setWrappers(wrappers1);
        ChannelFuture future
                = CommonClientCache.ROUTER.select(selector)
                .getChannelFuture();

        return future;
    }
}
