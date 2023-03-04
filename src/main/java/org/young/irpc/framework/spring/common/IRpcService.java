package org.young.irpc.framework.spring.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ClassName IRpcConfig
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午9:59
 * @Version 1.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IRpcService {

    int limit() default 0;

    String group() default "default";

    String serviceToken() default "";

}
