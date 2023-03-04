package org.young.irpc.framework.core.common.config;

import lombok.Data;

/**
 * @ClassName ClientConfig
 * @Description TODO
 * @Author young
 * @Date 2023/1/20 下午3:49
 * @Version 1.0
 **/
@Data
public class ClientConfig {

    /**
     * No need port
     */
//    private int port;

//    private String serverAddr;

    private String registerAddr;

    private String applicationName;

    private String selectorStrategy;

    private String serialization;

    private String group;

    private int packetMaxSize;

    private int maxRetryTimes;
}
