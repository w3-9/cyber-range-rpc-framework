package org.young.irpc.framework.core.server;

import lombok.Data;

/**
 * @ClassName ServerWrapper
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 下午3:56
 * @Version 1.0
 **/
@Data
public class ServerWrapper {

    Object serviceObj;

    private  String group = "default";

    private String token = "";

    private Integer flowLimit = 1;

    public ServerWrapper(Object serviceObj) {
        this.serviceObj = serviceObj;
    }

    public ServerWrapper(Object serviceObj, String group) {
        this.serviceObj = serviceObj;
        this.group = group;
    }

    public ServerWrapper(Object serviceObj, String group, String token) {
        this.serviceObj = serviceObj;
        this.group = group;
        this.token = token;
    }
}
