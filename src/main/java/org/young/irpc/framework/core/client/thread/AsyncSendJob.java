package org.young.irpc.framework.core.client.thread;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import org.young.irpc.framework.core.client.ConnectionHandler;
import org.young.irpc.framework.core.common.rpc.RpcInvocation;
import org.young.irpc.framework.core.common.rpc.RpcProtocol;
import org.young.irpc.framework.core.common.cache.CommonClientCache;

/**
 * @ClassName AsyncSendJob
 * @Description 使用该类起多线程完成任务：完成对连接的管理
 * @Author young
 * @Date 2023/1/20 下午5:06
 * @Version 1.0
 **/
@Data
public class AsyncSendJob implements Runnable{

//    ChannelFuture future;

    public AsyncSendJob() {
    }
//    public AsyncSendJob(ChannelFuture future) {
//        this.future = future;
//    }

    @Override
    public void run() {
        while (true) {
            try {

                Thread.sleep(2000);

                /**
                 * 从队列中获取rpc调用请求
                 */
                RpcInvocation invocation = CommonClientCache.CLIENT_SEND_QUEUE.take();
//                String jsonStr = JSON.toJSONString(invocation);
                /**
                 * 将请求转换为rpc协议实例，通过channelFuture发送给服务端
                 */
//                RpcProtocol protocol = new RpcProtocol(jsonStr.getBytes());
                RpcProtocol protocol = new RpcProtocol(
                        CommonClientCache.SERIALIZATION_FACTORY.serialize(invocation)
                );
                /**
                 * 重要的是futureChannel中的channel，一旦完成就不变了
                 */
//                this.future.channel().writeAndFlush(protocol);

                ChannelFuture future =
                        ConnectionHandler.getChannelFuture(invocation);
                future.channel().writeAndFlush(protocol);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
