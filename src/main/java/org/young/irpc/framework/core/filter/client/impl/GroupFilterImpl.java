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
 * @ClassName GroupFilterImpl
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 上午8:36
 * @Version 1.0
 **/
public class GroupFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> wrappers, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments()
                .get(RpcConstants.GROUP_TAG));
        if (!StrUtil.isEmpty(group)){


            Iterator iterator = wrappers.iterator();
            while (iterator.hasNext()){
                ChannelFutureWrapper wrapper = (ChannelFutureWrapper) iterator.next();
                if (!wrapper.getGroup().equals(group)) {
                    iterator.remove();
                }
            }

        }

        if (ListUtil.isEmpty(wrappers)){
            throw new RuntimeException("no available channel found for "+group);
        }

    }
}
