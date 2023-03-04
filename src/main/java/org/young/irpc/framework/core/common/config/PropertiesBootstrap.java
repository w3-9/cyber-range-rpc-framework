package org.young.irpc.framework.core.common.config;

import java.io.IOException;

import static org.young.irpc.framework.core.common.constant.ConfigConstant.*;

/**
 * @ClassName PropertiesBootstrap
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 下午5:06
 * @Version 1.0
 **/
public class PropertiesBootstrap {


    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setSerialization(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZATION));
        serverConfig.setGroup(PropertiesLoader.getPropertiesStr(SERVER_GROUP));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStr(SERVER_REGISTER_TYPE));
        serverConfig.setBizThreadsNum(PropertiesLoader.getPropertiesInteger(SERVER_BIZ_THREAD_NUM));
        serverConfig.setBlockQueueSize(PropertiesLoader.getPropertiesInteger(SERVER_BLOCKQUEUE_SIZE));
        serverConfig.setPacketMaxSize(PropertiesLoader.getPropertiesInteger(PACKET_MAX_SIZE));
        serverConfig.setMaxTCPConnection(PropertiesLoader.getPropertiesInteger(MAX_TCP_CONNECTION_NUM));
        serverConfig.setFlowLimit(PropertiesLoader.getPropertiesInteger(FLOW_LIMIT));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClientConfig config = new ClientConfig();
        config.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        config.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        config.setSelectorStrategy(PropertiesLoader.getPropertiesStr(CLIENT_SELECTOR_STRATEGY));
        config.setSerialization(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZATION));
        config.setGroup(PropertiesLoader.getPropertiesStr(CLIENT_GROUP));
        config.setPacketMaxSize(PropertiesLoader.getPropertiesInteger(PACKET_MAX_SIZE));
        config.setMaxRetryTimes(PropertiesLoader.getPropertiesInteger(MAX_RETRY_TIMES));
        return config;
    }

}
