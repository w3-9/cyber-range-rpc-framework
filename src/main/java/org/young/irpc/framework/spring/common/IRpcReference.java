package org.young.irpc.framework.spring.common;

import java.lang.annotation.*;

/**
 * @ClassName IRpcReference
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午9:58
 * @Version 1.0
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IRpcReference {

    String url() default "";

    String group() default "default";

    String serviceToken() default "";

    int retry() default 1;

    boolean async() default false;

}
