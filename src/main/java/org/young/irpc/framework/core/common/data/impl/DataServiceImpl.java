package org.young.irpc.framework.core.common.data.impl;

import lombok.extern.slf4j.Slf4j;
import org.young.irpc.framework.core.common.data.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DataServiceImpl
 * @Description 实现数据服务
 * @Author young
 * @Date 2023/1/23 下午10:32
 * @Version 1.0
 **/
@Slf4j
public class DataServiceImpl implements DataService {
    @Override
    public String sendData(String body) {
      log.info("Receiving data " + body.length());
      return "success";
    }

    @Override
    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("msg 1");
        list.add("msg 2");
        return list;
    }
}
