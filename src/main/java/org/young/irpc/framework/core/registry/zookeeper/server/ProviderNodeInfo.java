package org.young.irpc.framework.core.registry.zookeeper.server;

import lombok.Data;

/**
 * @ClassName ProviderNodeInfo
 * @Description TODO
 * @Author young
 * @Date 2023/2/15 下午10:49
 * @Version 1.0
 **/
@Data
public class ProviderNodeInfo {

    private String serviceName;

    private String address;

    private Integer weight;

    private String registerTime;

    private String group;

}
