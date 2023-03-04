package org.young.irpc.framework.core.common.rpc.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.constant.RpcConstants;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;

import java.util.List;

/**
 * @ClassName RpcDecoder
 * @Description 解码器，本质上是inbound adapter
 * @Author young
 * @Date 2023/1/17 下午8:02
 * @Version 1.0
 **/
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * BASE_LENGTH是协议开头的标准长度
     * Short(Magic_number) + int(contentLength)
     * 考虑到可以传空字符数组，所以最小不能小于2+4=6
     */
    public final int BASE_LENGTH = 2 + 4;

    public final int LENGTH_LIMIT = 4000;

    /**
     * 此处要解决粘包拆包的问题，而且还要设置请求数据包体积最大值。
     * todo 没有看到粘包拆包是如何解决的？ => 用的自定义协议，所以不会出现粘包、拆包的问题
     * @param ctx           the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in            the {@link ByteBuf} from which to read data
     * @param out           the {@link List} to which decoded messages should be added
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        //太小说明非正常字节流
        if (in.readableBytes() < this.BASE_LENGTH){
            return;
        }

        // 太大难以解析
        if (in.readableBytes() > this.LENGTH_LIMIT){
            log.warn("ReadableBytes too long.... skip");
            // 增大读指针，也就是将这一部分跳过去了，不读了！
            in.skipBytes(in.readableBytes());
        }

        int beginReader = in.readerIndex();
        in.markReaderIndex();

        /**
         * 如果不满足开头协议魔数，直接退出
         */
        if (in.readShort() != RpcConstants.MAGIC_NUMBER){
            ctx.close();
            return;
        }

        /**
         * 如果长度不够，说明需要重置读索引？
         * 为什么会不够呢？ todo
         */
        int length = in.readInt();
        if (in.readableBytes() < length){
            in.readerIndex(beginReader);
        }

        byte[] contents = new byte[length];
        in.readBytes(contents);

        RpcProtocol protocol = new RpcProtocol(contents);
        out.add(protocol);
    }
}
