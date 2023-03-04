package org.young.irpc.framework.core.filter.server.impl;

import org.young.irpc.framework.core.common.annotation.SPI;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.server.IServerFilter;

import java.util.concurrent.Semaphore;

/**
 * @ClassName ServiceLimitUnLockFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午9:34
 * @Version 1.0
 **/
@SPI("after")
public class ServiceLimitUnLockFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation invocation) {
        Semaphore semaphore = CommonServiceCache.SERVER_FLOW_LIMIT_SEMAPHORE.get(invocation.getTargetServiceName());
        semaphore.release();
    }
}
