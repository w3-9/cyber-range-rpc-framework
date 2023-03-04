package org.young.irpc.framework.core.dispatcher.server.thread;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.exception.IRpcException;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;
import org.young.irpc.framework.core.dispatcher.server.ServerChannelReadData;

import java.lang.reflect.Method;

import static org.young.irpc.framework.core.common.cache.CommonServiceCache.PROVIDER_SERVICE_MAP;

/**
 * @ClassName BizHandler
 * @Description TODO
 * @Author young
 * @Date 2023/3/1 下午7:48
 * @Version 1.0
 **/
@Data
@Slf4j
@AllArgsConstructor
public class BizHandler implements Runnable{

    private ServerChannelReadData readData;

    @Override
    public void run() {



        log.warn(Thread.currentThread().getName()
        +" : performing biz logics");

        RpcProtocol protocol = readData.getProtocol();
        ChannelHandlerContext ctx = this.readData.getChannelHandlerContext();


        /**
         * 此时Object类型可以转换
         */

        /**
         * 原来FASTJSON 已被替换
         */
        // 这里content就包含调用服务的信息 =》 应该是json格式
//        String content = new String(protocol.getContent(), 0, protocol.getContentLength());

//        log.info("getting protocol msg => "+ content);

        // 将调用信息转换为RpcInvocation，里面包含RPC的调用信息

        //RpcInvocation invocation = JSON.parseObject(content, RpcInvocation.class);

        RpcInvocation invocation = CommonServiceCache.SERIALIZATION_FACTORY.deserialize(protocol.getContent(),RpcInvocation.class);

        try {
            CommonServiceCache.SERVER_FILTER_CHAIN_BEFORE.doFilter(invocation);
        }catch (Exception e){
            invocationError(invocation,e);
            wrapInvocationAndSend(invocation,ctx);
            return;
        }

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        Object aimObject = PROVIDER_SERVICE_MAP.get(invocation.getTargetServiceName());

        /**
         * 方法不止一个
         */
        Method[] methods = aimObject.getClass().getDeclaredMethods();

        Object result = null;

        /**
         * 遍历方法，确定到底调用哪一个
         */
        for (Method method : methods){
            if (method.getName().equals(invocation.getTargetMethod())){
                // 即使返回值为空也没问题？
                // 没关系 此时result = null
                /**
                 * 此处完成RPC最终调用
                 */

                try {
                    result = method.invoke(aimObject,invocation.getArgs());
                } catch (Exception e) {
                    invocationError(invocation,e);
                }
                break;
            }
        }

        if (result != null){
            log.info("getting result =>"+ result.toString());
        }else{
            log.info("get result => null");
        }

        try {
            CommonServiceCache.SERVER_FILTER_CHAIN_AFTER.doFilter(invocation);
        }catch (Exception e){
            invocationError(invocation,e);
            wrapInvocationAndSend(invocation,ctx);
            return;
        }

        invocation.setResponse(result);

        /**
         * 重新打包，发送出去，将值基于writeAndFlush返回
         */
//        RpcProtocol resProtocol = new RpcProtocol(JSON.toJSONString(invocation).getBytes());

        wrapInvocationAndSend(invocation,ctx);

    }

    private void wrapInvocationAndSend(RpcInvocation invocation,
                                       ChannelHandlerContext ctx){
        RpcProtocol resProtocol
                = new RpcProtocol(CommonServiceCache.SERIALIZATION_FACTORY.serialize(invocation));
        ctx.writeAndFlush(resProtocol);
    }

    private void invocationError(RpcInvocation invocation,
                                 Exception e){


        if (e instanceof IRpcException){
            invocation.setError(e);
        }else{
            e.printStackTrace();
            invocation.setResponse(new RuntimeException("Server Internal Error"));
        }
    }
}
