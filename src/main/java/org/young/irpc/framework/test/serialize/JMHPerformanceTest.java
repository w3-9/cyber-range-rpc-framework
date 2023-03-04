package org.young.irpc.framework.test.serialize;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.young.irpc.framework.core.serialize.SerializeFactory;
import org.young.irpc.framework.core.serialize.impl.FastJsonSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.HessianSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.JDKSerializeFactory;
import org.young.irpc.framework.core.serialize.impl.KryoSerializeFactory;
import org.young.irpc.framework.test.common.TestUser;

/**
 * @ClassName JMHPerformanceTest
 * @Description TODO
 * @Author young
 * @Date 2023/2/24 下午8:44
 * @Version 1.0
 **/

@Slf4j
public class JMHPerformanceTest {


    private static TestUser generateUser(){
        TestUser user = new TestUser();
        user.setAge(15);
        user.setName("testUser");
        user.setAddress("Hongkong");
        user.setMail("user@qq.com");
        return user;
    }

    @Benchmark
    public void jdkTest(){
        SerializeFactory serializeFactory
                = new JDKSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
//        log.debug("jdk : "+bytes.length);
        serializeFactory.deserialize(bytes,TestUser.class);
    }
    @Benchmark
    public void hessianTest(){
        SerializeFactory serializeFactory
                = new HessianSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
//        log.debug("Hessian : "+bytes.length);
        serializeFactory.deserialize(bytes,TestUser.class);

    }

    @Benchmark
    public void fastJsonTest(){
        SerializeFactory serializeFactory
                = new FastJsonSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
//        log.debug("FastJson : "+bytes.length);
        serializeFactory.deserialize(bytes,TestUser.class);

    }

    @Benchmark
    public void kryoTest(){
        SerializeFactory serializeFactory
                = new KryoSerializeFactory();
        byte[] bytes
                = serializeFactory.serialize(generateUser());
//        log.debug("Kyro : "+bytes.length);
        serializeFactory.deserialize(bytes,TestUser.class);

    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().warmupIterations(2).measurementBatchSize(1)
                .forks(1).build();
        new Runner(options).run();
    }
}
