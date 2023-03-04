package org.young.irpc.framework.core.filter.client;

import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.IFilter;
import org.young.irpc.framework.core.filter.server.IServerFilter;
import org.young.irpc.framework.core.spi.ExtensionLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName IClinetFilter
 * @Description TODO
 * @Author young
 * @Date 2023/2/25 下午8:41
 * @Version 1.0
 **/
public interface IClientFilter extends IFilter {

    Class clazz = IClientFilter.class;

    /**
     * 这里面和轮询策略怎么处理
     * @param wrappers
     * @param rpcInvocation
     */
    public void doFilter(List<ChannelFutureWrapper> wrappers,
                  RpcInvocation rpcInvocation);


    public static  void load() throws Exception{
        ExtensionLoader.loadExtention(clazz);
    }
    public static IClientFilter getServerFilterThroughSPI(String className) throws Exception {
        return (IClientFilter) ExtensionLoader.getInstance(clazz,className);
    }

    public static List<IClientFilter> loadAndGetAllServerFilters() throws Exception {
        load();
        List<Object> objects = ExtensionLoader.getAllInstances(clazz);
        List<IClientFilter> filters = new ArrayList<>();
        for (Object object : objects){
            filters.add((IClientFilter) object);
        }
        return filters;
    }

}
