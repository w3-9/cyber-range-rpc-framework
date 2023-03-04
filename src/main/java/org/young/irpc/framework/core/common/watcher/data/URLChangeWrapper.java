package org.young.irpc.framework.core.common.watcher.data;

import lombok.Data;

import java.util.List;

/**
 * @ClassName URLChangeWrapper
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午10:57
 * @Version 1.0
 **/
@Data
public class URLChangeWrapper {

    private String serviceName;


    private List<String> providerURL;

}
