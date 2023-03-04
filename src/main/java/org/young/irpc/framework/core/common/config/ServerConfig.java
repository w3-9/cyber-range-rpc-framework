package org.young.irpc.framework.core.common.config;

import lombok.Data;

/**
 * @ClassName ServerConfig
 * @Description TODO
 * @Author young
 * @Date 2023/1/17 下午7:39
 * @Version 1.0
 **/
@Data
public class ServerConfig {

    private int port;

    private String registerAddr;

    private String applicationName;

    private String serialization;

    private String group;

    private String registerType;

    private int blockQueueSize;

    private int bizThreadsNum;

    private int packetMaxSize;

    private int maxTCPConnection;

    private int flowLimit;
}
