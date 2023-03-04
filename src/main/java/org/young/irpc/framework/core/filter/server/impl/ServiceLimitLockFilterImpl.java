package org.young.irpc.framework.core.filter.server.impl;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.annotation.SPI;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.exception.impl.RpcFlowLimitExceedException;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.server.IServerFilter;

import java.util.concurrent.Semaphore;

/**
 * @ClassName ServiceLimitLockFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午9:34
 * @Version 1.0
 **/
@SPI("before")
@Slf4j
public class ServiceLimitLockFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation invocation) {
        log.warn("starting flow limit check");

        Semaphore semaphore = CommonServiceCache.SERVER_FLOW_LIMIT_SEMAPHORE.get(invocation.getTargetServiceName());

        boolean acquireResult = semaphore.tryAcquire();

        System.out.println(acquireResult);

        if (!acquireResult){

            log.warn("Flow exceeds limit... reject request "+invocation.getUuid());
            throw new RpcFlowLimitExceedException("Flow exceeds limit");
        }

    }
}
