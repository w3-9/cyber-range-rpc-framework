package org.young.irpc.framework.core.common.constant.enums;

import org.young.irpc.framework.core.serialize.SerializeFactory;
import org.young.irpc.framework.core.serialize.impl.FastJsonSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.HessianSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.JDKSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.KryoSerializeFactory;
import org.young.irpc.framework.core.spi.ExtensionLoader;

import java.io.IOException;
import java.security.cert.Extension;
import java.util.HashMap;

/**
 * @ClassName SerializationEnum
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午9:46
 * @Version 1.0
 **/
public enum SerializationEnum {
    fastjson("fastjson"),
    jdk("jdk"),
    hessian("hessian"),
    kryo("kryo");

    private final String text;

    SerializationEnum(final String text){
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }

    /**
     * Original Version
     * @param value
     * @return
     */
    public static SerializeFactory getFactory(String value){
        switch (SerializationEnum.valueOf(value)){
            case jdk:
                return  new JDKSerializeFactory();
            case kryo:
                return new KryoSerializeFactory();
            case hessian:
                return new HessianSerializeFactory();
            case fastjson:
                return new FastJsonSerializeFactory();
            default:
                return new JDKSerializeFactory();
        }
    }


}
