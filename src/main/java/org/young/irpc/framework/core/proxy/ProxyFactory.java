package org.young.irpc.framework.core.proxy;

import org.young.irpc.framework.core.client.RpcReferenceWrapper;

/**
 * @ClassName ProxyFactory
 * @Description 辅助客户端发起调用的代理对象
 * @Author young
 * @Date 2023/1/23 下午9:28
 * @Version 1.0
 **/
public interface ProxyFactory {

    <T> T getProxy(final Class clazz) throws Throwable;

    <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable;

}
