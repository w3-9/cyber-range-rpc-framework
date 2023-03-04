package org.young.irpc.framework.core.filter.client.impl;

import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.util.ListUtil;
import org.young.irpc.framework.core.common.util.StrUtil;
import org.young.irpc.framework.core.filter.client.IClientFilter;

import java.util.Iterator;
import java.util.List;

/**
 * @ClassName DirectInvokeFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 上午11:18
 * @Version 1.0
 **/
public class DirectInvokeFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> wrappers, RpcInvocation rpcInvocation) {
        String url = String.valueOf(rpcInvocation.getAttachments()
                .getOrDefault(RpcConstants.URL_TAG,null));
        if (StrUtil.isEmpty(url)){
            return;
        }

        Iterator iterator = wrappers.iterator();
        while (iterator.hasNext()){
            ChannelFutureWrapper wrapper = (ChannelFutureWrapper) iterator.next();
            if (!StrUtil.isEmpty(url) && !wrapper.getAddr().equals(url)) {
                iterator.remove();
            }
        }


        if (ListUtil.isEmpty(wrappers)){
            throw new RuntimeException("no available channel found for "+url);
        }
    }
}
