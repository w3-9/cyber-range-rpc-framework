package org.young.irpc.framework.core.common.watcher.listener.impl;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.util.ListUtil;
import org.young.irpc.framework.core.common.watcher.data.URLChangeWrapper;
import org.young.irpc.framework.core.common.watcher.event.impl.IrpcUpdateEvent;
import org.young.irpc.framework.core.common.watcher.listener.IrpcListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ServiceUpdateListener
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午11:59
 * @Version 1.0
 **/
@Slf4j
public class ServiceUpdateListener implements IrpcListener<IrpcUpdateEvent> {
    /**
     * 回调函数
     * @param t
     */
    @Override
    public void callback(Object t) {
        URLChangeWrapper wrapper = (URLChangeWrapper) t;
        /**
         * 已连接的
         */
        List<ChannelFutureWrapper> channelFutureWrapperList
                = CommonClientCache.CONNECT_MAP
                .get(wrapper.getServiceName());
        if (ListUtil.isEmpty(channelFutureWrapperList)){
            log.error("ChannelFutureWrapper is Empty..");
            return;
        }

        /**
         * 当前的
         */
        List<String> matchProviderUrl = wrapper.getProviderURL();
        Set<String> finalUrl = new HashSet<>();
        List<ChannelFutureWrapper> finalChannelWrappers =  new ArrayList<>();
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrapperList){
            String oldServerAddress = channelFutureWrapper.getAddr();
            if (!matchProviderUrl.contains(oldServerAddress)){
                continue;
            }else{
                finalChannelWrappers.add(channelFutureWrapper);
                finalUrl.add(oldServerAddress);
            }
        }

        List<ChannelFutureWrapper> addedChanelFutureWrapper
                = new ArrayList<>();
        for (String newProviderUrl : matchProviderUrl){
            if (!finalUrl.contains(newProviderUrl)){
                ChannelFutureWrapper channelFutureWrapper =
                        ChannelFutureWrapper.createChannelFutureWrapperFromAddr(newProviderUrl);
                if (channelFutureWrapper!=null){
                    addedChanelFutureWrapper.add(channelFutureWrapper);
                    finalUrl.add(newProviderUrl);
                }
            }
        }
        finalChannelWrappers.addAll(addedChanelFutureWrapper);
        /**
         * 更新连接池
         */
        CommonClientCache.CONNECT_MAP
                .put(wrapper.getServiceName(),finalChannelWrappers);

    }
}
