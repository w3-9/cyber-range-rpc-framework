package org.young.irpc.framework.core.serialize.impl;

import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.io.*;

/**
 * @ClassName JDKSerializeFactory
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午6:06
 * @Version 1.0
 **/
public class JDKSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream  oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            oos.flush();
            oos.close();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            return (T)o;
        } catch (IOException |  ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
