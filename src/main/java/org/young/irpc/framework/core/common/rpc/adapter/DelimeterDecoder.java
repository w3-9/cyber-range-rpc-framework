package org.young.irpc.framework.core.common.rpc.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.young.irpc.framework.core.common.constant.RpcConstants;

/**
 * @ClassName DelimeterDecoder
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午2:31
 * @Version 1.0
 **/
public class DelimeterDecoder extends DelimiterBasedFrameDecoder {

    private static ByteBuf delimeter = Unpooled.copiedBuffer(RpcConstants.DELIMTER_SIGN.getBytes());

    public DelimeterDecoder(int maxPacketLength){


        super(maxPacketLength,delimeter);
    }

}
