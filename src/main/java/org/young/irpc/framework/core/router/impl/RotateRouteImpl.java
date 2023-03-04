package org.young.irpc.framework.core.router.impl;

import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.registry.URL;
import org.young.irpc.framework.core.router.IRouter;
import org.young.irpc.framework.core.router.Selector;

import java.util.List;

/**
 * @ClassName RotateRouteImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/23 下午4:28
 * @Version 1.0
 **/
public class RotateRouteImpl implements IRouter {
    @Override
    public void refreshRouterArray(Selector selector) {
        List<ChannelFutureWrapper> wrappers
                = CommonClientCache.CONNECT_MAP.get(
                        selector.getProviderServiceName()
                );
        int size = wrappers.size();
        ChannelFutureWrapper[] wrapperArr = new ChannelFutureWrapper[size];
        for (int i = 0; i<size; i++){
            wrapperArr[i] = wrappers.get(i);
        }
        CommonClientCache.SERVER_ROUTER_MAP
                .put(selector.getProviderServiceName(),
                        wrapperArr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CommonClientCache.REF.get(selector);
    }

    @Override
    public void updateWeight(URL url) {
        /**
         * do nothing
         */
    }
}
