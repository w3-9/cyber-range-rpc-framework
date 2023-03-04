package org.young.irpc.framework.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.young.irpc.framework.core.server.Server;
import org.young.irpc.framework.core.server.ServerWrapper;
import org.young.irpc.framework.spring.common.IRpcService;

import java.util.Map;

/**
 * @ClassName ServerAutoConfiguration
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午10:19
 * @Version 1.0
 **/
@Slf4j
@Configuration("ServerAutoConfiguration")
public class ServerAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = new Server();
        Map<String,Object> beanMap
                = applicationContext.getBeansWithAnnotation(IRpcService.class);
        this.logInfo();
        server.initServerConfig();
        for (String beanName : beanMap.keySet()){
            Object bean = beanMap.get(beanName);
            IRpcService iRpcService = bean.getClass().getAnnotation(IRpcService.class);
            ServerWrapper wrapper = new ServerWrapper(
                    bean,
                    iRpcService.group(),
                    iRpcService.serviceToken()
            );
            wrapper.setFlowLimit(iRpcService.limit());
            server.registerService(wrapper);
            log.warn("register service "+beanName);
        }
        server.startApplication();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void logInfo(){
        log.warn("Booting up server...");

    }
}
