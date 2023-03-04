package org.young.irpc.framework.core.serialize.impl;

import com.alibaba.fastjson.JSON;
import org.young.irpc.framework.core.serialize.SerializeFactory;

/**
 * @ClassName FastJsonSerializeFactory
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午7:06
 * @Version 1.0
 **/
public class FastJsonSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        String jsonStr = JSON.toJSONString(t);
        return jsonStr.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes,clazz);
    }
}
