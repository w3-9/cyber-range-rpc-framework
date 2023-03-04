package org.young.irpc.framework.core.common.watcher.listener;

/**
 * @ClassName IrpcListener
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午11:13
 * @Version 1.0
 **/
public interface IrpcListener<T> {

    void callback(Object t);

}
