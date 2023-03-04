package org.young.irpc.framework.core.common.rpc.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;

/**
 * @ClassName RpcEncoder
 * @Description 报文编码，获取输入，转成对应报文的Bytes
 * encode就是将类转换为bytes
 * @Author young
 * @Date 2023/1/17 下午8:03
 * @Version 1.0
 **/
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {

    /**\
     * 应该是从中萃取msg的信息，写入out变成bytes
     * 但不是编码器么，为什么没看到编码内容
     * 其实是理解失误了，RpcEncode通过继承MessageToByteEncoder，
     * 实际上继承了ChannelOutboundHandlerAdapter，也就是将RpcProtocol变成bytes发出去
     * @param ctx           the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg           the message to encode
     * @param out           the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) throws Exception {
        out.writeShort(msg.getMagicNumber());
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
        out.writeBytes(RpcConstants.DELIMTER_SIGN.getBytes());
    }
}
