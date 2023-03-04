package org.young.irpc.framework.core.dispatcher.server.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.dispatcher.server.ServerChannelReadData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName ServerBizJobHandler
 * @Description TODO
 * @Author young
 * @Date 2023/3/1 下午7:44
 * @Version 1.0
 **/
@Slf4j
@Data
@AllArgsConstructor
public class ServerBizJobHandler implements Runnable{

    private BlockingQueue<ServerChannelReadData> blockingQueue;

    private ExecutorService executorService;


    @Override
    public void run() {
        while (true){

            try {
                ServerChannelReadData readData
                        = this.blockingQueue.take();

                log.info("incoming new requests ...");

                this.executorService.submit(new BizHandler(readData));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }
}
