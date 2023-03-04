package org.young.irpc.framework.core.proxy.jdk;

import org.young.irpc.framework.core.client.RpcReference;
import org.young.irpc.framework.core.client.RpcReferenceWrapper;
import org.young.irpc.framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @ClassName JdkProxyFactory
 * @Description 实现了getProxy方法
 * 看一下英文解释：Returns an instance of a proxy class
 * for the specified interfaces that
 * dispatches method invocations to the specified invocation handler
 * 也就是说是对特定接口的代理类，将方法调用接引到特殊的调用处理器上
 * @Author young
 * @Date 2023/1/23 下午9:28
 * @Version 1.0
 **/
public class JdkProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class clazz) throws Throwable {
        /**
         * 指定clazz所对应的类，就是将这个类（接口）的信息指引了
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new JdkClientInvocationHandler(clazz)
        );
    }

    @Override
    public <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable {
        return (T) Proxy.newProxyInstance(
                rpcReferenceWrapper.getAimClass().getClassLoader(),
                new Class[]{rpcReferenceWrapper.getAimClass()},
                new JdkClientInvocationHandler(rpcReferenceWrapper)
        );
    }
}
