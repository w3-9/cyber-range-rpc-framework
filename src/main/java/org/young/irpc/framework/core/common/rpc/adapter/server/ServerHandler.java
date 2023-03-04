package org.young.irpc.framework.core.common.rpc.adapter.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.cache.CommonServiceCache;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;
import org.young.irpc.framework.core.dispatcher.server.ServerChannelReadData;

import java.lang.reflect.Method;

import static org.young.irpc.framework.core.common.cache.CommonServiceCache.PROVIDER_SERVICE_MAP;

/**
 * @ClassName ServerHandler
 * @Description ChannelInboundHandlerAdapter 是进站处理器
 * @Author young
 * @Date 2023/1/17 下午8:07
 * @Version 1.0
 **/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读到消息以后如何处理？
     * 此时，消息已经是bytes了
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol protocol = (RpcProtocol) msg;
        ServerChannelReadData readData
                = new ServerChannelReadData();
        readData.setProtocol(protocol);
        readData.setChannelHandlerContext(ctx);
        CommonServiceCache.SERVER_DISPATCHER
                .add(readData);
    }

    /**
     * 出错的话关闭连接
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()){
            log.warn("Exception occurred during channel running, closing channel...");
            channel.close();
        }
    }
}
