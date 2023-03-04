package org.young.irpc.framework.core.dispatcher.server;

import lombok.Data;

import java.util.concurrent.*;
import org.young.irpc.framework.core.dispatcher.server.thread.ServerBizJobHandler;

/**
 * @ClassName ServerChannelDispatcher
 * @Description TODO
 * @Author young
 * @Date 2023/3/1 下午7:12
 * @Version 1.0
 **/
@Data
public class ServerChannelDispatcher {

    private final int ARRAY_BLOCKING_QUEUE_SIZE = 512;

    private BlockingQueue<ServerChannelReadData> blockingQueue;

    private ExecutorService executorService;

    public void init(int queueSize, int bizThreadNum){
        blockingQueue = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(bizThreadNum, bizThreadNum,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(512));
    }

    public void add(ServerChannelReadData readData){
        this.blockingQueue.add(readData);
    }


    public void consume(){
        Thread thread = new Thread(new ServerBizJobHandler(this.blockingQueue,this.executorService));
        thread.start();
    }

}
