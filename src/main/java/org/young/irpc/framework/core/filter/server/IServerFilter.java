package org.young.irpc.framework.core.filter.server;

import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.filter.IFilter;
import org.young.irpc.framework.core.spi.ExtensionLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName IServerFilter
 * @Description TODO
 * @Author young
 * @Date 2023/2/25 下午8:47
 * @Version 1.0
 **/
public interface IServerFilter extends IFilter {

    Class clazz = IServerFilter.class;

    void doFilter(RpcInvocation invocation) ;

    static  void load() throws Exception{
        ExtensionLoader.loadExtention(clazz);
    }
    static IServerFilter getServerFilterThroughSPI(String className) throws Exception {
        return (IServerFilter) ExtensionLoader.getInstance(clazz,className);
    }

    static List<IServerFilter> loadAndGetAllServerFilters() throws Exception {
        load();
        List<Object> objects = ExtensionLoader.getAllInstances(clazz);
        List<IServerFilter> filters = new ArrayList<>();
        for (Object object : objects){
            filters.add((IServerFilter) object);
        }
        return filters;
    }

    static List<IServerFilter> loadAndGetCorrespondFilters(String tag) throws Exception{
        load();
        List<IServerFilter> filters = new ArrayList<>();
        List<Object> objects = ExtensionLoader.getInstanceByAnnotationTag(clazz,tag);
        for (Object object : objects){
            filters.add((IServerFilter) object);
        }
        return filters;
    }
}
