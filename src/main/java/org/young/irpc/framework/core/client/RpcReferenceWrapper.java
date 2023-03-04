package org.young.irpc.framework.core.client;

import lombok.Data;
import org.young.irpc.framework.core.common.constant.RpcConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName RpcReferenceWrapper
 * @Description TODO
 * @Author young
 * @Date 2023/2/26 下午7:07
 * @Version 1.0
 **/
@Data
public class RpcReferenceWrapper<T> {


    private Class<T> aimClass;

    private Map<String,Object> attachments = new ConcurrentHashMap<>();

    public void setAsync(boolean async){
        this.attachments.put(RpcConstants.ASYNC_TAG,async);
    }

    public boolean isAsync(){
        return (Boolean) this.attachments.get(RpcConstants.ASYNC_TAG);
    }

    public String getUrl(){
        return String.valueOf(this.attachments.get(RpcConstants.URL_TAG));
    }

    public void setUrl(String url){
        this.attachments.put(RpcConstants.URL_TAG,url);
    }

    public void setGroup(String group){
        this.attachments.put(RpcConstants.GROUP_TAG,group);
    }

    public String getGroup(){
        return String.valueOf(RpcConstants.GROUP_TAG);
    }

    public void setToken(String group){
        this.attachments.put(RpcConstants.TOKEN_TAG,group);
    }

    public String getToken(){
        return String.valueOf(RpcConstants.TOKEN_TAG);
    }


}
