package org.young.irpc.framework.core.common.rpc.adapter.client;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;
import org.young.irpc.framework.core.common.cache.CommonClientCache;

/**
 * @ClassName ClientHandler
 * @Description 这是基于线程池完成的
 * 线程名称为nioEventLoopGroup-2-1
 * @Author young
 * @Date 2023/1/20 下午5:00
 * @Version 1.0
 **/
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 此时消息已经经过解码
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol protocol = (RpcProtocol) msg;


        /**
         * 获取内容
         */
//        String content = new String(protocol.getContent(), 0, protocol.getContentLength());
//        log.info("getting protocol msg => "+ content);

        /**
         * 转换为请求对象及结果
         */
//        RpcInvocation invocation = JSON.parseObject(content, RpcInvocation.class);

        RpcInvocation invocation =
                CommonClientCache.SERIALIZATION_FACTORY.deserialize(
                        protocol.getContent(),
                        RpcInvocation.class
                );



        /**
         * 获得的结果匹配不上则报错！
         */
        if (!CommonClientCache.RESP_MAP.containsKey(invocation.getUuid())){
            log.error("uuid of Request out of data : "+invocation.getUuid());
            ReferenceCountUtil.release(msg);
            return;
//            throw new IllegalArgumentException("Server response error...");
        }


        /**
         * 如果Error 直接打印并结束
         */
        if (invocation.getError()!=null){
            invocation.getError().printStackTrace();
            ReferenceCountUtil.release(msg);
            return;
        }

        if ((boolean)(invocation.getAttachments().getOrDefault(RpcConstants.ASYNC_TAG,false))){
            log.warn("Async cmd -> " + invocation.getResponse());
            ReferenceCountUtil.release(msg);
            return;
        }

        /**
         * 更新结果到RESP_MAP里
         */
        CommonClientCache.RESP_MAP.put(invocation.getUuid(),invocation);
        /**
         * 需要释放msg，防止内存泄漏
         */
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * 如出现异常则直接关掉
         */
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()){
            log.warn("Exception occurred during channel running, closing channel...");
            channel.close();
        }
    }
}
