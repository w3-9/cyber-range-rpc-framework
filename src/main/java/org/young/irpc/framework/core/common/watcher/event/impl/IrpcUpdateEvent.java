package org.young.irpc.framework.core.common.watcher.event.impl;

import org.young.irpc.framework.core.common.watcher.event.IrpcEvent;

/**
 * @ClassName IrpcUpdateEvent
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午11:02
 * @Version 1.0
 **/
public class IrpcUpdateEvent implements IrpcEvent {

    private Object data;

    public IrpcUpdateEvent(Object data){
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public IrpcEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
