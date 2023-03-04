package org.young.irpc.framework.core.router;

import lombok.Data;
import org.young.irpc.framework.core.common.channel.ChannelFutureWrapper;

import java.util.List;

/**
 * @ClassName Selector
 * @Description TODO
 * @Author young
 * @Date 2023/2/21 下午10:06
 * @Version 1.0
 **/
@Data
public class Selector {

    private String providerServiceName;

    private ChannelFutureWrapper[] wrappers;

}
