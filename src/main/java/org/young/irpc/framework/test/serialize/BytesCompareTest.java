package org.young.irpc.framework.test.serialize;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.serialize.SerializeFactory;
import org.young.irpc.framework.core.serialize.impl.FastJsonSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.HessianSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.JDKSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.KryoSerializeFactory;
import org.young.irpc.framework.test.common.TestUser;

/**
 * @ClassName BytesCompareTest
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午8:05
 * @Version 1.0
 **/
@Slf4j
public class BytesCompareTest {

    private static TestUser generateUser(){
        TestUser user = new TestUser();
        user.setAge(15);
        user.setName("testUser");
        user.setAddress("Hongkong");
        user.setMail("user@qq.com");
        return user;
    }

    public void jdkTest(){
        SerializeFactory serializeFactory
                = new JDKSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
        log.debug("jdk : "+bytes.length);
    }

    public void hessianTest(){
        SerializeFactory serializeFactory
                = new HessianSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
        log.debug("Hessian : "+bytes.length);
    }

    public void fastJsonTest(){
        SerializeFactory serializeFactory
                = new FastJsonSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
        log.debug("FastJson : "+bytes.length);
    }

    public void kryoTest(){
        SerializeFactory serializeFactory
                = new KryoSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
        log.debug("Kyro : "+bytes.length);
    }

    public static void main(String[] args) {
        BytesCompareTest
                test = new BytesCompareTest();
        test.fastJsonTest();
        test.jdkTest();
        test.hessianTest();
        test.kryoTest();
    }
}
