package org.young.irpc.framework.core.router;

import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.server.AbstractRegistry;
import org.young.irpc.framework.core.spi.ExtensionLoader;


/**
 * @ClassName IRouter
 * @Description TODO
 * @Author young
 * @Date 2023/2/21 下午9:09
 * @Version 1.0
 **/
public interface IRouter {

    Class clazz = IRouter.class;

    void refreshRouterArray(Selector selector);

    ChannelFutureWrapper select(Selector selector);

    void updateWeight(URL url);

    static  void load() throws Exception{
        ExtensionLoader.loadExtention(clazz);
    }

    static IRouter getFactoryThroughSPI(String value) throws Exception {
        return (IRouter) ExtensionLoader.getInstance(clazz,value);
    }

    static IRouter loadAndgetInstance(String value) throws Exception{
        load();
        return getFactoryThroughSPI(value);
    }
}
