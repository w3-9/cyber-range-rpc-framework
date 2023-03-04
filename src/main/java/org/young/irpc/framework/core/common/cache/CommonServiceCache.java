package org.young.irpc.framework.core.common.cache;

import org.young.irpc.framework.core.common.config.ServerConfig;
import org.young.irpc.framework.core.dispatcher.server.ServerChannelDispatcher;
import org.young.irpc.framework.core.filter.server.ServerFilterChain;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.serialize.SerializeFactory;
import org.young.irpc.framework.core.server.ServerWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @ClassName CommonServiceCache
 * @Description TODO
 * @Author young
 * @Date 2023/1/17 下午8:25
 * @Version 1.0
 **/
public class CommonServiceCache {

    /**
     * 用于存储接口名及对应service实例
     */
    public static Map<String,Object> PROVIDER_SERVICE_MAP = new HashMap<>();

    public static Set<URL> PROVIDER_URL_SET = new HashSet<>();

    public static SerializeFactory SERIALIZATION_FACTORY;

    public static ServerFilterChain SERVER_FILTER_CHAIN_BEFORE;
    public static ServerFilterChain SERVER_FILTER_CHAIN_AFTER;

    public static Map<String, ServerWrapper> PROVIDER_SERVER_WRAPPER
            = new ConcurrentHashMap<>();

    public static ServerConfig SERVER_CONFIG;

    public static ServerChannelDispatcher SERVER_DISPATCHER = new ServerChannelDispatcher();

    public static Map<String, Semaphore> SERVER_FLOW_LIMIT_SEMAPHORE = new ConcurrentHashMap<>();
}
