package org.young.irpc.framework.core.dispatcher.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;

/**
 * @ClassName ServerChannelReadData
 * @Description TODO
 * @Author young
 * @Date 2023/3/1 下午7:13
 * @Version 1.0
 **/
@Data
public class ServerChannelReadData {

    private RpcProtocol protocol;

    private ChannelHandlerContext channelHandlerContext;

}
