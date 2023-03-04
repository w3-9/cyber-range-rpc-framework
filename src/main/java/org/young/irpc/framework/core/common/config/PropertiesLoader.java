package org.young.irpc.framework.core.common.config;

import org.young.irpc.framework.core.common.util.StrUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName PrppertiesLoader
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 下午5:46
 * @Version 1.0
 **/
public class PropertiesLoader {


    private static Properties properties;
    private static Map<String,String> propertiesMap = new HashMap<>();
    private static String DEFAULT_PROPERTIES_FILE = "/home/young/IdeaProjects/rpc/irpc-framework/src/main/resources/irpc.properties";

    public static void loadConfiguration() throws IOException {
        if (properties!=null){
            return;
        }
        properties = new Properties();
        FileInputStream in = new FileInputStream(DEFAULT_PROPERTIES_FILE);
        properties.load(in);

    }

    public static Integer getPropertiesInteger(String key){
        if (properties == null){
            return null;
        }
        if (StrUtil.isEmpty(key)){
            return null;
        }
        if (!propertiesMap.containsKey(key)){
            String value = properties.getProperty(key);
            propertiesMap.put(key,value);
        }
        return Integer.valueOf(propertiesMap.getOrDefault(key,null));
    }

    public static String getPropertiesStr(String key){
        if (properties == null){
            return null;
        }
        if (StrUtil.isEmpty(key)){
            return null;
        }
        if (!propertiesMap.containsKey(key)){
            String value = properties.getProperty(key);
            propertiesMap.put(key,value);
        }
        return String.valueOf(propertiesMap.getOrDefault(key,null));
    }


}
