package org.young.irpc.framework.core.common.rpc;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName RPCInvocation
 * @Description RPC请求对象（如何调用的），也就是说，这里才是RPC传送报文里的字节内容
 * @Author young
 * @Date 2023/1/20 下午12:53
 * @Version 1.0
 **/
@Data
public class RpcInvocation implements Serializable {

    private static final long serialVersionUID = 1949734262531844277L;
    /**
     * 调用方法
     */
    private String targetMethod;

    /**
     * 请求的目标服务名称
     */
    private String targetServiceName;

    /**
     * 请求目标参数
     */
    private Object[] args;

    /**
     * 主要是用于匹配请求和响应的一个关键值。
     * 当请求从客户端发出的时候，会有一个uuid用于记录发出的请求
     * 待数据返回的时候通过uuid来匹配对应的请求线程
     */
    private String uuid;

    /**
     * reponse 直接放在同一个请求里么？
     *
     */
    private Object response;

    private Throwable error;

    private Map<String,Object> attachments = new ConcurrentHashMap<>();

    private int remainingRetryTimes;
}
