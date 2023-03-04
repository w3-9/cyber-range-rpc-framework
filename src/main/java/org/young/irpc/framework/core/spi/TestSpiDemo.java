package org.young.irpc.framework.core.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @ClassName TestSpi
 * @Description TODO
 * @Author young
 * @Date 2023/2/28 上午11:15
 * @Version 1.0
 **/
public class TestSpiDemo {

    public static void doTest(ISpiTest iSpiTest){
        System.out.println("before...");
        iSpiTest.doTest();
        System.out.println("after");
    }


    /**
     * JDK内置提供的ServiceLoader会自动帮助我们去加载/META-INF/services/目录下边的文件，
     * 并且将其转换为具体实现类
     * @param args
     */
    public static void main(String[] args) {
        ServiceLoader<ISpiTest>
                serviceLoader = ServiceLoader.load(ISpiTest.class);
        Iterator<ISpiTest> iterator
                = serviceLoader.iterator();
        while (iterator.hasNext()){
            doTest(iterator.next());
        }
    }
}
