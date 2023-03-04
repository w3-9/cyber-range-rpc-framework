package org.young.irpc.framework.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.young.irpc.framework.core.client.Client;
import org.young.irpc.framework.core.client.ConnectionHandler;
import org.young.irpc.framework.core.client.RpcReference;
import org.young.irpc.framework.core.client.RpcReferenceWrapper;
import org.young.irpc.framework.spring.common.IRpcReference;

import java.lang.reflect.Field;

/**
 * @ClassName ClientAutoConfiguration
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午10:07
 * @Version 1.0
 **/
@Slf4j
@DependsOn("ServerAutoConfiguration")
@Configuration
public class ClientAutoConfiguration implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    private static RpcReference rpcReference = null;
    private static Client client = null;
    private volatile boolean needInitClient = false;
    private volatile boolean hasInitClientConfig = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (client!=null && needInitClient){
            ConnectionHandler.setBootstrap(client.getBootstrap());
            client.doConnectServer();
            client.startClient();
        }

    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields){
            if (field.isAnnotationPresent(IRpcReference.class)){
                if (!hasInitClientConfig){
                    client = new Client();

                    try {
                        rpcReference = client.initClienApplication();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    hasInitClientConfig = true;
                }

                needInitClient  = true;
                IRpcReference iRpcReference = field.getAnnotation(IRpcReference.class);

                try{
                    field.setAccessible(true);
                    Object reflectionObj = field.get(bean);
                    RpcReferenceWrapper wrapper = new RpcReferenceWrapper();
                    wrapper.setAimClass(field.getType());
                    wrapper.setGroup(iRpcReference.group());
                    wrapper.setToken(iRpcReference.serviceToken());
                    wrapper.setUrl(iRpcReference.url());
                    wrapper.setAsync(iRpcReference.async());

                    reflectionObj = rpcReference.get(wrapper);
                    field.set(bean,reflectionObj);
                    client.subscribeService(field.getType());
                }catch (Exception e){
                    e.printStackTrace();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

            }
        }



        return bean;
    }
}
