package org.young.irpc.framework.spring.test.service.impl;

import org.young.irpc.framework.spring.common.IRpcService;
import org.young.irpc.framework.spring.test.service.TestStrService;

/**
 * @ClassName TestServiceImpl1
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 上午11:24
 * @Version 1.0
 **/
@IRpcService(limit = 3,serviceToken = "token1",group = "dev")
public class TestServiceImpl1 implements TestStrService {
    @Override
    public String test(String s) {
        return "Service 1 : " + s;
    }
}
