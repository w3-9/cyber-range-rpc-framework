package org.young.irpc.framework.core.common.watcher.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.util.ListUtil;
import org.young.irpc.framework.core.common.util.ReflectUtil;
import org.young.irpc.framework.core.common.watcher.event.IrpcEvent;
import org.young.irpc.framework.core.common.watcher.listener.IrpcListener;
import org.young.irpc.framework.core.common.watcher.listener.impl.ProviderNodeDataChangeListener;
import org.young.irpc.framework.core.common.watcher.listener.impl.ServiceUpdateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName IrpcListenerLoader
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 上午11:10
 * @Version 1.0
 **/
@Data
@Slf4j
public class IrpcListenerLoader {

    private static final int POOL_SIZE = 2;

    private static List<IrpcListener> irpcListenerList = new ArrayList<>();

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(POOL_SIZE);

    public static void registerListener(IrpcListener listener){
        irpcListenerList.add(listener);
    }

    public void init(){

        registerListener(new ServiceUpdateListener());
        registerListener(new ProviderNodeDataChangeListener());
    }

    public static void sendEvent(IrpcEvent event){
        if (ListUtil.isEmpty(irpcListenerList)){
            log.warn("No listener inited...");
            return;
        }

        for (IrpcListener<?> irpcListener : irpcListenerList){
            Class<?> type = ReflectUtil.getInterfaceT(irpcListener);
            if (type.equals(event.getClass())){
                eventThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        irpcListener.callback(event.getData());
                    }
                });
            }
        }
    }



}
