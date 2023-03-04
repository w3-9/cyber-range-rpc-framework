package org.young.irpc.framework.core.common.util;

/**
 * @ClassName StrUtil
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 下午6:12
 * @Version 1.0
 **/
public class StrUtil {

    public static boolean isEmpty(String str){
        return str==null || str.isEmpty() || str.equals("null");
    }

}
