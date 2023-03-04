package org.young.irpc.framework.core.spi;


import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.annotation.SPI;
import org.young.irpc.framework.core.filter.server.IServerFilter;
import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ExtensionLoader
 * @Description TODO
 * @Author young
 * @Date 2023/2/28 下午6:36
 * @Version 1.0
 **/
@Slf4j
public class ExtensionLoader {

    public static final String
        EXTENSION_LOADER_PREFIX = "META-INF/irpc/";

    private static final String IGNORE_TAG = "#";
    private static final String EQUAL_TAG = "=";

    public static final ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();

    public static Map<String, LinkedHashMap<String,Class>>
        EXTENSION_LOADER_CLASS_CACHE = new ConcurrentHashMap<>();



    public static void loadExtention(Class clazz) throws IOException, ClassNotFoundException {
        EXTENSION_LOADER.load(clazz);
    }

    public void load(Class clazz) throws IOException, ClassNotFoundException {
        if (clazz == null){
            throw new IllegalArgumentException("Class not found...");
        }

        String spiFilePath = EXTENSION_LOADER_PREFIX + clazz.getName();
        ClassLoader loader = this.getClass().getClassLoader();

        Enumeration<URL> enumeration = loader.getResources(spiFilePath);
        while (enumeration.hasMoreElements()){
            URL url = enumeration.nextElement();
            InputStreamReader reader = new InputStreamReader(url.openStream());
            BufferedReader buf = new BufferedReader(reader);

            String line;
            LinkedHashMap<String,Class> classMap = new LinkedHashMap<>();
            while ((line=buf.readLine())!=null){
                if (line.startsWith(IGNORE_TAG)){
                    continue;
                }
                String[] kvs = line.split(EQUAL_TAG);
                if (kvs.length!=2){
                    continue;
                }
                classMap.put(kvs[0],Class.forName(kvs[1]));
            }

            if (EXTENSION_LOADER_CLASS_CACHE.containsKey(clazz.getName())){
                EXTENSION_LOADER_CLASS_CACHE.get(clazz.getName())
                        .putAll(classMap);
            }else{
                EXTENSION_LOADER_CLASS_CACHE.put(clazz.getName(),
                        classMap);
            }
        }
    }

    public static Object getInstance(Class clazz,String classImpl) throws Exception {
        if (!EXTENSION_LOADER_CLASS_CACHE.containsKey(clazz.getName())){
            throw new IllegalArgumentException("no Clazz found for "+clazz.getName());
        }
        LinkedHashMap<String,Class> classMap = EXTENSION_LOADER_CLASS_CACHE.get(clazz.getName());
        if (classMap.get(classImpl)==null){
            throw new IllegalArgumentException("no class_instance found for "+classImpl);
        }
        log.warn("Generating instance for "+classImpl);
        return classMap.get(classImpl).newInstance();
    }

    public static List<Object> getAllInstances(Class clazz) throws Exception{
        loadExtention(clazz);
        if (!EXTENSION_LOADER_CLASS_CACHE.containsKey(clazz.getName())){
            throw new IllegalArgumentException("no Clazz found for "+clazz.getName());
        }
        LinkedHashMap<String,Class> classMap = EXTENSION_LOADER_CLASS_CACHE.get(clazz.getName());
        List<Object> list = new ArrayList<>();
        for (Class target : classMap.values()){
            log.warn("generating "+target.getName());
            list.add(target.newInstance());
        }
        return list;
    }

    public static Object getOneInstance(Class clazz, String className) throws Exception {
        loadExtention(clazz);
        return getInstance(clazz,className);
    }

    public static List<Object> getInstanceByAnnotationTag(Class clazz, String tag) throws InstantiationException, IllegalAccessException {
        List<Object> list = new ArrayList<>();
        for (Class insClazz : EXTENSION_LOADER_CLASS_CACHE
                .get(clazz.getName()).values()){
            SPI spi = (SPI)insClazz.getDeclaredAnnotation(SPI.class);
            if (spi.value().equals(tag)){
                log.warn("generating "+insClazz.getName());
                list.add(insClazz.newInstance());
            }
        }
        return list;
    }
}
