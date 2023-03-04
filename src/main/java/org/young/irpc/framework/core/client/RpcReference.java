package org.young.irpc.framework.core.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.young.irpc.framework.core.proxy.ProxyFactory;

/**
 * @ClassName RpcReference
 * @Description 目前设计比较冗余
 * @Author young
 * @Date 2023/1/20 下午4:51
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class RpcReference {

    public ProxyFactory proxyFactory;


//    public <T> T get(Class<T> clazz) throws Throwable {
//        return proxyFactory.getProxy(clazz);
//    }

    public <T> T  get(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable{
        return proxyFactory.getProxy(rpcReferenceWrapper);
    }

}
