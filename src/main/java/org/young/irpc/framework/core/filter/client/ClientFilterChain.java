package org.young.irpc.framework.core.filter.client;

import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.server.IServerFilter;
import org.young.irpc.framework.core.filter.server.ServerFilterChain;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ClientFilterChain
 * @Description TODO
 * @Author young
 * @Date 2023/2/25 下午8:54
 * @Version 1.0
 **/
public class ClientFilterChain {

    private static List<IClientFilter> filters
            = new ArrayList<>();

    public ClientFilterChain add(IClientFilter filter){
        filters.add(filter);
        return this;
    }

    public void doFilter(List<ChannelFutureWrapper> wrappers,RpcInvocation invocation){
        for (IClientFilter filter : filters){
            filter.doFilter(wrappers,invocation);
        }
    }



    public void AddFilters() throws Exception {

        filters.addAll(IClientFilter.loadAndGetAllServerFilters());

    }

}
