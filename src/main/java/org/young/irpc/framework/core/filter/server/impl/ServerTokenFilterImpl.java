package org.young.irpc.framework.core.filter.server.impl;

import org.young.irpc.framework.core.common.annotation.SPI;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.exception.impl.RpcTokenNotVerifiedException;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.util.StrUtil;
import org.young.irpc.framework.core.filter.server.IServerFilter;
import org.young.irpc.framework.core.server.ServerWrapper;

/**
 * @ClassName ServerTokenFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 下午1:55
 * @Version 1.0
 **/
@SPI("before")
public class ServerTokenFilterImpl implements IServerFilter {


    @Override
    public void doFilter(RpcInvocation invocation)  {
        String token
                = String.valueOf(invocation.getAttachments()
                .get(RpcConstants.TOKEN_TAG));
        ServerWrapper serverWrapper
                = CommonServiceCache.PROVIDER_SERVER_WRAPPER
                .get(invocation.getTargetServiceName());
        String tokenServer
                = serverWrapper.getToken();
        if (StrUtil.isEmpty(tokenServer)){
            return;
        }
        if (!StrUtil.isEmpty(token) && token.equals(tokenServer)){
            return;
        }
        throw new RpcTokenNotVerifiedException("Token not verified...");
    }
}
