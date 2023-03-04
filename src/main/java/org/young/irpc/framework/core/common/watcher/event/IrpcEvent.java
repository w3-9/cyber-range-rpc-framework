package org.young.irpc.framework.core.common.watcher.event;

/**
 * @ClassName IrpcEvent
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午11:00
 * @Version 1.0
 **/
public interface IrpcEvent {

    Object getData();

    IrpcEvent setData(Object data);

}
