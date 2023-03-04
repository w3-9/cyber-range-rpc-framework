package org.young.irpc.framework.core.common.cache;

import org.young.irpc.framework.core.common.channel.ChannelFuturePoolingRef;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.config.ClientConfig;
import org.young.irpc.framework.core.filter.client.ClientFilterChain;
import org.young.irpc.framework.core.router.IRouter;
import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CommonClientCache
 * @Description TODO
 * @Author young
 * @Date 2023/1/20 下午5:11
 * @Version 1.0
 **/
public class CommonClientCache {

    public static BlockingQueue<RpcInvocation> CLIENT_SEND_QUEUE = new ArrayBlockingQueue<>(256);

    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();

    /**
     * 订阅的服务列表
     */
    public static List<String> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    public static Map<String,Map<String,String>> URL_MAP = new ConcurrentHashMap<>();

    public static ClientConfig CLIENT_CONFIG;

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    public static Map<String,ChannelFutureWrapper[]> SERVER_ROUTER_MAP = new HashMap<>();

    public static IRouter ROUTER;

    public static ChannelFuturePoolingRef REF = new ChannelFuturePoolingRef();

    public static SerializeFactory SERIALIZATION_FACTORY;

    public static ClientFilterChain CLIENT_FILTER_CHAIN;

}
