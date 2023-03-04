package org.young.irpc.framework.core.registry;

import lombok.Data;
import org.young.irpc.framework.core.registry.zookeeper.server.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

/**
 * @ClassName URL
 * @Description 注册包含信息传递类，也可以理解为被写入ZooKeeper中的对象代表类
 * @Author young
 * @Date 2023/2/13 下午10:52
 * @Version 1.0
 **/
@Data
public class URL {

    /**
     * 为防止硬编码，将参数定义为变量
     */
    public  final static String HOST_STRING = "host";
    private final static String HOST_STRING_DEFAULT = "none";
    public final static String PORT_STRING = "port";
    private final static String PORT_STRING_DEFAULT = "8888";

    private final static int SERVICE_WEIGHT = 200;

    public final static String SEPERATOR = ";";

    public final static String HOST_PORT_SEPERATOR = ":";

    public final static String SLASH = "/";

    public final static String SERVICE_PATH = "servicepath";

    public final static String PROVIDER_IPs = "providerIps";

    public final static String GROUP_STRING = "group";

    public final static String GROUP_STRING_DEFAULT = "default";

    public final static String LIMIT_STRING = "limit";

    public final static String LIMIT_STRING_DEFAULT = "2";

    /**
     * 服务应用名称，通过读取properties文件获得
     * @ProPertiesBootstrap irpc.applicationName 变量
     */
    private String applicationName;

    /**
     * 服务名称（一个应用可以有多个服务）
     * 为注册到Zookeeper节点的服务名称
     */
    private String serviceName;

    /**
     * 参数字典，存储 Host Port等信息
     * 如果是指代服务，则可以为空
     */
    private Map<String,String> paramMap = new HashMap<>();

    public void addParam(String key, String value){
        this.paramMap.putIfAbsent(key,value);
    }

    /**
     * 基于URL对象，生成供应者URL字符串，写入Zookeeper对应路径下
     * provider下存放的数据以Application:Service:ip:Port的格式存储。
     * @param url
     * @return
     */
    public static String providerURL(URL url){
        String host = url.getParamMap().getOrDefault(HOST_STRING,HOST_STRING_DEFAULT);
        String port = url.getParamMap().getOrDefault(PORT_STRING,PORT_STRING_DEFAULT);
        String group = url.getParamMap().getOrDefault(GROUP_STRING,GROUP_STRING_DEFAULT);
        return new String(
                (url.applicationName
                +SEPERATOR
                +url.getServiceName()
                +SEPERATOR
                +host
                +HOST_PORT_SEPERATOR
                +port
                +SEPERATOR
                +System.currentTimeMillis()
                +SEPERATOR
                +SERVICE_WEIGHT
                +SEPERATOR
                +group).getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
    }

    /**
     * 基于URL对象，生成消费者URL字符串
     * Application:Service:Host
     * @todo 这个URL是干什么用的？
     * @param url
     * @return
     */
    public static String consumerURL(URL url){
        String host = url.getParamMap().getOrDefault(HOST_STRING,HOST_STRING_DEFAULT);
        return new String((url.applicationName
                +SEPERATOR
                +url.getServiceName()
                +SEPERATOR
                +host
                +SEPERATOR
                +System.currentTimeMillis()
        ).getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
    }

    /**
     * 从ProviderStr中拆分出服务者信息
     * @param str
     * @return ProviderNodeInfo
     * todo 没太理清楚这个是在哪里用到的
     */
    public static ProviderNodeInfo buildProviderFromString(String str){
        String[] items = str.split(SLASH);
        ProviderNodeInfo nodeInfo = new ProviderNodeInfo();
        nodeInfo.setServiceName(items[1]);
        nodeInfo.setAddress(items[2]);
        nodeInfo.setRegisterTime(items[3]);
        nodeInfo.setWeight(Integer.valueOf(items[4]));
        nodeInfo.setGroup(items[5]);
        return nodeInfo;
    }

    /**
     * 根据Url生成服务者地址，可以理解为key
     * @param url
     * @return
     */
    public static String getProviderAddress(URL url){
        String host = url.getParamMap().getOrDefault(HOST_STRING,HOST_STRING_DEFAULT);
        String port = url.getParamMap().getOrDefault(PORT_STRING,PORT_STRING_DEFAULT);
        return new String(
                (host+SEPERATOR+port).getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
    }

    /**
     * 根据URL生成消费者地址
     * @param url
     * @return
     */
    public static String getConsumerAddress(URL url){
        String host = url.getParamMap().getOrDefault(HOST_STRING,HOST_STRING_DEFAULT);
        return url.getApplicationName()+SEPERATOR+host;
    }
}
