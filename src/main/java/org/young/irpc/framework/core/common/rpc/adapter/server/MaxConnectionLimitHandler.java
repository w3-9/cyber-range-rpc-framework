package org.young.irpc.framework.core.common.rpc.adapter.server;

import io.netty.channel.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName MaxConnectionLimitHandler
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午8:00
 * @Version 1.0
 **/
@Slf4j
@Data
public class MaxConnectionLimitHandler extends ChannelInboundHandlerAdapter {

    private int maxConnectionNum;

    private final AtomicInteger curConnectionNum = new AtomicInteger();

    private final AtomicLong droppedConnectionNum = new AtomicLong();

    private final Set<Channel> childChannel = Collections.newSetFromMap(new ConcurrentHashMap<>());


    public MaxConnectionLimitHandler(int maxConnectionNum) {
        this.maxConnectionNum = maxConnectionNum;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = (Channel) msg;
        int conn = curConnectionNum.incrementAndGet();

        if (conn <= this.maxConnectionNum){
            this.childChannel.add(channel);
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    childChannel.remove(future);
                    curConnectionNum.decrementAndGet();
                }
            });
            super.channelRead(ctx,msg);
        }else{
            log.error("have to give up connection ... " + channel.remoteAddress().toString());
            this.curConnectionNum.decrementAndGet();
            // socket.close()方法立即返回，OS放弃发送缓冲区的数据直接向对端发送RST包，对端收到复位错误
            channel.config().setOption(ChannelOption.SO_LINGER, 0);
            channel.unsafe().closeForcibly();
            droppedConnectionNum.incrementAndGet();
        }


    }
}
