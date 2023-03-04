package org.young.irpc.framework.core.serialize;

import org.young.irpc.framework.core.spi.ExtensionLoader;

/**
 * @ClassName SerializeFactory
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午6:00
 * @Version 1.0
 **/
public interface SerializeFactory {

    Class clazz = SerializeFactory.class;

    <T> byte[] serialize(T t);

    <T> T deserialize(byte[] bytes, Class<T> clazz);

    public static void load() throws Exception{
        ExtensionLoader.loadExtention(clazz);
    }
    public static SerializeFactory getFactoryThroughSPI(String value) throws Exception {
        return (SerializeFactory) ExtensionLoader.getInstance(clazz,value);
    }

    public static SerializeFactory loadAndgetInstance(String value) throws Exception{
        load();
        return getFactoryThroughSPI(value);
    }
}
