package org.young.irpc.framework.core.filter.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.client.IClientFilter;

import java.util.List;

/**
 * @ClassName ClientLogFilter
 * @Description TODO
 * @Author young
 * @Date 2023/2/25 下午9:18
 * @Version 1.0
 **/
@Slf4j
public class ClientLogFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> wrappers, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments()
                .put(RpcConstants.CLIENT_APP_NAME_TAG, CommonClientCache.CLIENT_CONFIG.getApplicationName());
        log.info(CommonClientCache.CLIENT_CONFIG.getApplicationName() +": invoke "+rpcInvocation.getTargetServiceName());
    }
}
