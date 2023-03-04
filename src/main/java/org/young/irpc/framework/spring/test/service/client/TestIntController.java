package org.young.irpc.framework.spring.test.service.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.young.irpc.framework.spring.common.IRpcReference;
import org.young.irpc.framework.spring.common.IRpcService;
import org.young.irpc.framework.spring.test.service.TestIntService;
import org.young.irpc.framework.spring.test.service.TestStrService;

/**
 * @ClassName TestIntController1
 * @Description TODO
 * @Author young
 * @Date 2023/3/4 下午12:40
 * @Version 1.0
 **/
@RestController
@RequestMapping("/test")
public class TestIntController {

    @IRpcReference(serviceToken = "token2",group = "dev")
    TestIntService intService;

    @IRpcReference(serviceToken = "token1",group = "dev")
    TestStrService strService;

    @GetMapping("/intService")
    public int getInt(@RequestParam("val") int x){
        return intService.test(x);
    }

    @GetMapping("/StrService")
    public String getStr(@RequestParam("val") String x){
        return strService.test(x);
    }

}
