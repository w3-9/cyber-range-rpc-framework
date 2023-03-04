package org.young.irpc.framework.core.common.watcher.event.impl;

import lombok.Data;
import org.young.irpc.framework.core.common.watcher.event.IrpcEvent;
import org.young.irpc.framework.core.registry.zookeeper.server.ProviderNodeInfo;

/**
 * @ClassName IrpcDataChangeEvent
 * @Description TODO
 * @Author young
 * @Date 2023/2/23 下午12:59
 * @Version 1.0
 **/
@Data
public class IrpcDataChangeEvent implements IrpcEvent {

    private ProviderNodeInfo nodeInfo;

    public IrpcDataChangeEvent(ProviderNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public Object getData() {

        return this.nodeInfo;
    }

    @Override
    public IrpcEvent setData(Object data) {

        this.nodeInfo = (ProviderNodeInfo) data;
        return this;
    }
}
