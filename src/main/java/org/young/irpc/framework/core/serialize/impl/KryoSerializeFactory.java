package org.young.irpc.framework.core.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.young.irpc.framework.core.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @ClassName KyroSerializeFactory
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午6:41
 * @Version 1.0
 **/
public class KryoSerializeFactory implements SerializeFactory {

    private static final ThreadLocal<Kryo> kryos
            = new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            return kryo;
        }
    };

    @Override
    public <T> byte[] serialize(T t) {
        Output output = null;

        try{
            Kryo kryo = kryos.get();
            ByteArrayOutputStream byteArrayOutputStream
                    = new ByteArrayOutputStream();
            output = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(output,t);
            return output.toBytes();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (output !=null){
                output.close();
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Input input = null;
        try{
            Kryo kryo = kryos.get();
            ByteArrayInputStream byteArrayInputStream =
                    new ByteArrayInputStream(bytes);
            input = new Input(byteArrayInputStream);
            return (T)kryo.readClassAndObject(input);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (input!=null){
                input.close();
            }
        }

    }
}
