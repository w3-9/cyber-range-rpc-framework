package org.young.irpc.framework.core.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ClassName ReflectUtil
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 下午2:54
 * @Version 1.0
 **/
public class ReflectUtil {

    public static Class<?> getInterfaceT(Object o){
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType
                = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>){
            return (Class<?>)type;
        }
        return null;
    }

}
