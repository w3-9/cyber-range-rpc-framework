package org.young.irpc.framework.core.serialize.impl;

import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * @ClassName HessianSerializeFactory
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午6:20
 * @Version 1.0
 **/
public class HessianSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] bytes = null;


        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(bos);
            output.writeObject(t);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null){
            return null;
        }
        Object res = null;

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Hessian2Input input = new Hessian2Input(is);
            res = input.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return (T)res;
    }
}
