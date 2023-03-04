package org.young.irpc.framework.core.common.watcher.listener.impl;

import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.watcher.event.impl.IrpcDataChangeEvent;
import org.young.irpc.framework.core.common.watcher.listener.IrpcListener;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.registry.zookeeper.server.ProviderNodeInfo;

import java.util.List;

/**
 * @ClassName ProviderNodeDataChangeListener
 * @Description TODO
 * @Author young
 * @Date 2023/2/23 下午1:10
 * @Version 1.0
 **/
public class ProviderNodeDataChangeListener implements IrpcListener<IrpcDataChangeEvent> {
    @Override
    public void callback(Object t) {
        ProviderNodeInfo nodeInfo
                = (ProviderNodeInfo) t;
        List<ChannelFutureWrapper> wrappers
                = CommonClientCache.CONNECT_MAP.get(nodeInfo.getServiceName());
        for (ChannelFutureWrapper wrapper : wrappers){
            String address
                    = wrapper.getAddr();
            if (address.equals(nodeInfo.getAddress())){
                wrapper.setWeight(nodeInfo.getWeight());

                /**
                 * 只是说明
                 */
                URL url = new URL();
                url.setServiceName(nodeInfo.getServiceName());
                CommonClientCache.ROUTER.updateWeight(url);
                break;
            }
        }
    }
}
