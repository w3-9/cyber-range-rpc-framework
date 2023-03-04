package org.young.irpc.framework.core.common.constant;

/**
 * @ClassName ConfigConstant
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午2:21
 * @Version 1.0
 **/
public class ConfigConstant {

    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "irpc.serverPort";
    public static final String REGISTER_ADDRESS = "irpc.registerAddr";
    public static final String APPLICATION_NAME="irpc.applicationName";

    public static final String CLIENT_SELECTOR_STRATEGY="irpc.routerStrategy";

    public static final String SERVER_SERIALIZATION="irpc.serverSerialization";

    public static final String CLIENT_SERIALIZATION="irpc.clientSerialization";

    public static final String SERVER_GROUP="irpc.serverGroup";

    public static final String CLIENT_GROUP="irpc.clientGroup";

    public static final String SERVER_REGISTER_TYPE = "irpc.registerType";

    public static final String SERVER_BLOCKQUEUE_SIZE = "irpc.serverTaskQueueSize";

    public static final String SERVER_BIZ_THREAD_NUM = "irpc.serverBizThreadsNum";

    public static final String PACKET_MAX_SIZE = "irpc.packetMaxSize";

    public static final String MAX_RETRY_TIMES = "irpc.maxRetryTimes";

    public static final String MAX_TCP_CONNECTION_NUM = "irpc.maxTcpConnection";

    public static final String FLOW_LIMIT = "irpc.flowlimit";

}
