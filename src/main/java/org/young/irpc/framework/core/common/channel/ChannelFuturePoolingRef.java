package org.young.irpc.framework.core.common.channel;

import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelFuture;
import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.router.Selector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName ChannelFuturePoolingRef
 * @Description TODO
 * @Author young
 * @Date 2023/2/21 下午10:45
 * @Version 1.0
 **/
@Slf4j
public class ChannelFuturePoolingRef {

    private Map<String,AtomicLong> referTimes = new HashMap<>();

    public ChannelFutureWrapper get(Selector selector){
        String serviceName = selector.getProviderServiceName();
        ChannelFutureWrapper[] wrappers = selector.getWrappers();
        if (!referTimes.containsKey(serviceName)){
            referTimes.put(serviceName,new AtomicLong(0));
        }
        AtomicLong refer = referTimes.get(serviceName);
        long i = refer.getAndIncrement();
        ChannelFutureWrapper res = wrappers[(int)(i % (wrappers.length))];
        log.info("Selector : select "+res.getAddr());
        return res;
    }

}
