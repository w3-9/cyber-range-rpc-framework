package org.young.irpc.framework.core.filter.server.impl;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.annotation.SPI;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.server.IServerFilter;

/**
 * @ClassName ServerLogFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 下午1:32
 * @Version 1.0
 **/
@Slf4j
@SPI("before")
public class ServerLogFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation invocation) {
        log.info(String.valueOf(invocation.getAttachments().get(RpcConstants.CLIENT_APP_NAME_TAG))
        + " invoke "+ invocation.getTargetServiceName() + " : "
        + invocation.getTargetMethod());
    }
}
