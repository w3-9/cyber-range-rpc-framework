package org.young.irpc.framework.spring.test.service.impl;

import org.young.irpc.framework.spring.common.IRpcService;
import org.young.irpc.framework.spring.test.service.TestIntService;

/**
 * @ClassName TestServiceImpl2
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午11:24
 * @Version 1.0
 **/
@IRpcService(limit = 3,serviceToken = "token2",group = "dev")
public class TestServiceImpl2 implements TestIntService {
    @Override
    public int test(int i) {
        return i*2;
    }
}
