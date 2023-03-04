package org.young.irpc.framework.core.registry;

import org.young.irpc.framework.core.filter.server.IServerFilter;
import org.young.irpc.framework.core.serialize.SerializeFactory;
import org.young.irpc.framework.core.spi.ExtensionLoader;

/**
 * @ClassName RegisterService
 * @Description 注册服务，为服务端和客户端准备的
 * 主要的动作行为就是：服务的注册，下线，订阅，取消订阅。
 * 这四个动作可以看作是远程服务信息的四个核心元操作
 * @Author young
 * @Date 2023/2/13 下午10:29
 * @Version 1.0
 **/
public interface RegisterService {

    void register(URL url);

    void unregister(URL url);

    void subscribe(URL url);

    void unsubscribe(URL url);


}
