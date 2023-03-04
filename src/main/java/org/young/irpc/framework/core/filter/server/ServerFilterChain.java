package org.young.irpc.framework.core.filter.server;

import lombok.Data;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ServerFilterChain
 * @Description TODO
 * @Author young
 * @Date 2023/2/25 下午8:48
 * @Version 1.0
 **/
@Data
public class ServerFilterChain {

    private final String SPI;

    public ServerFilterChain(String SPI) throws Exception {
        this.SPI = SPI;
        this.addFilters();
    }

    private List<IServerFilter> filters
            = new ArrayList<>();

    public void addFilters() throws Exception {

        filters.addAll(IServerFilter.loadAndGetCorrespondFilters(this.SPI));

    }

    public ServerFilterChain add(IServerFilter filter){
        filters.add(filter);
        return this;
    }

    public void doFilter(RpcInvocation invocation) {
        for (IServerFilter filter : filters){
            filter.doFilter(invocation);
        }
    }
}
