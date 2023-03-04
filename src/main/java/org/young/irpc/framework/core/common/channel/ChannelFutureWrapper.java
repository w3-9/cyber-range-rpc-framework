package org.young.irpc.framework.core.common.channel;

import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.young.irpc.framework.core.client.ConnectionHandler;

/**
 * @ClassName ChannelFutureWrapper
 * @Description TODO
 * @Author young
 * @Date 2023/2/17 下午2:51
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
public class ChannelFutureWrapper {

    private ChannelFuture channelFuture;

    private String host;

    private Integer port;

    private Integer weight;

    private String group;

    public ChannelFutureWrapper(String host, Integer port, Integer weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public String getAddr(){
        return host+":"+port;
    }

    /**
     * 基于地址构建ChannelFutureWrapper
     * @param serverAddress
     * @return
     */
    public static ChannelFutureWrapper createChannelFutureWrapperFromAddr(String serverAddress){
        ChannelFutureWrapper wrapper =
                new ChannelFutureWrapper();
        String host = serverAddress.split(":")[0];
        int port = Integer.valueOf(serverAddress.split(":")[1]);
        wrapper.setHost(host);
        wrapper.setPort(port);
        try {
            ChannelFuture future
                    = ConnectionHandler.createChannelFuture(
                            host,
                            port
                    );
            wrapper.setChannelFuture(future);
            return wrapper;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

}
