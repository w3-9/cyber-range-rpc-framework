package org.young.irpc.framework.core.spi.impl;

import org.young.irpc.framework.core.spi.ISpiTest;

/**
 * @ClassName DefaultISpiTest
 * @Description TODO
 * @Author young
 * @Date 2023/2/28 上午10:50
 * @Version 1.0
 **/
public class DefaultISpiTest implements ISpiTest {
    @Override
    public void doTest() {
        System.out.println("spi : defaultISpiTest...");
    }
}
