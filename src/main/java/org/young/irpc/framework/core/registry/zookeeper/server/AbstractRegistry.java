package org.young.irpc.framework.core.registry.zookeeper.server;

import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.registry.RegisterService;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.spi.ExtensionLoader;

import java.util.List;
import java.util.Map;

/**
 * @ClassName AbstractRegistry
 * @Description 对注册数据作统一处理，形成基本方法
 * @Author young
 * @Date 2023/2/15 下午10:58
 * @Version 1.0
 **/
public abstract class AbstractRegistry implements RegisterService {

    static Class clazz = AbstractRegistry.class;

    @Override
    public void register(URL url) {
        /**
         * 仅放在数据层完成必要操作，剩下的则交给实现类
         * 此时并为在Zookeeper中注册
         */
        CommonServiceCache.PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unregister(URL url) {
        CommonServiceCache.PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        CommonClientCache.SUBSCRIBE_SERVICE_LIST.add(url.getServiceName());
    }

    @Override
    public void unsubscribe(URL url) {
        CommonClientCache.SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }

    public abstract void doAfterSubscribe(URL url);

    public abstract void doBeforeSubscribe(URL url);

    /**
     * 给定服务名称，获取服务者的ip信息
     * @param serviceName
     * @return
     */
    public abstract List<String> getProviderIps(String serviceName);

    public abstract Map<String,String> getServiceWeightMap(String serviceName);

    public static  void load() throws Exception{
        ExtensionLoader.loadExtention(clazz);
    }

    public static AbstractRegistry getFactoryThroughSPI(String value) throws Exception {
        return (AbstractRegistry) ExtensionLoader.getInstance(clazz,value);
    }

    public static AbstractRegistry loadAndgetInstance(String value) throws Exception{
        load();
        return getFactoryThroughSPI(value);
    }
}
