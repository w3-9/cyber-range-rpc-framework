package org.young.irpc.framework.core.proxy.jdk;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.client.RpcReferenceWrapper;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.cache.CommonClientCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName JdkClientInvocationHandler
 * @Description 就是在dataservice调用前后，引发本处理器
 * @Author young
 * @Date 2023/1/23 下午9:35
 * @Version 1.0
 **/
@Slf4j
public class JdkClientInvocationHandler implements InvocationHandler {

    /**
     * 这个Object是干什么用的，为什么每次都要放进去？
     */
    private final static Object object = new Object();

    /**
     * 发送到获取反应的时间
     */
    private final long TIMEOUT_DELAY = 4000;

    /**
     * 调用类，通过反射去invoke，客户端应该至少有被调用类的信息
     */
    private Class clazz;

    private RpcReferenceWrapper wrapper;

    public JdkClientInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    public JdkClientInvocationHandler(RpcReferenceWrapper wrapper) {
        this.wrapper = wrapper;
        this.clazz = wrapper.getAimClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        /**
         * 新建请求，将对应类信息放进去
         */
        RpcInvocation invocation = new RpcInvocation();
        invocation.setArgs(args);
        invocation.setUuid(UUID.randomUUID().toString());
        invocation.setTargetMethod(method.getName());
        invocation.setTargetServiceName(clazz.getName());
        invocation.setAttachments(wrapper.getAttachments());
        invocation.setRemainingRetryTimes(CommonClientCache.CLIENT_CONFIG.getMaxRetryTimes());

        CommonClientCache.RESP_MAP.put(invocation.getUuid(),object);
        CommonClientCache.CLIENT_SEND_QUEUE.put(invocation);

        /**
         * 只是不堵塞等待结果 不代表结果不返回
         */
        if (wrapper.isAsync()){
            log.warn("Sending async requests....");
            Thread.sleep(1000);
            return null;
        }

        long beginTime = System.currentTimeMillis();

        /**
         * 这里没有调用method.invoke，是因为是远程调用，所以我们不调用
         * 放到队列，由另一个线程负责发送
         * 但是，这个也是单线程的，main，需要等待
         * 在某个函数中需要顺序调用A，B，C，D四个RPC接口，
         * 如果有多个线程来执行发送操作（只是负责发送部分逻辑），
         * 那么如何确保A，B，C，D的调用顺序是有序的呢？
         * 如果是单个线程专门负责发送调用请求，这种问题就可以避免掉了
         */

        while (System.currentTimeMillis() < beginTime + TIMEOUT_DELAY || invocation.getRemainingRetryTimes() > 0){

            long currentTime = System.currentTimeMillis();
            Object object  = CommonClientCache.RESP_MAP.get(invocation.getUuid());
            // result has been updated
            if (object instanceof RpcInvocation){
                log.info("Response ... => "+ ((RpcInvocation)object).toString());
                return ((RpcInvocation)object).getResponse();
            }

            if (currentTime > beginTime + TIMEOUT_DELAY){
                log.error("Retry for request "+invocation.getUuid()
                +"... "+(invocation.getRemainingRetryTimes()-1) +" time remaining");
                beginTime = currentTime;
                invocation.setRemainingRetryTimes(invocation.getRemainingRetryTimes()-1);
                CommonClientCache.CLIENT_SEND_QUEUE.put(invocation);
            }

        }

        CommonClientCache.RESP_MAP.remove(invocation.getUuid());
        throw new TimeoutException("Server Response Timeout....");
    }
}
