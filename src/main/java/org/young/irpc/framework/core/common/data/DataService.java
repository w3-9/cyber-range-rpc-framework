package org.young.irpc.framework.core.common.data;

import java.util.List;

/**
 * 数据服务接口
 */
public interface DataService {

    public String sendData(String body);

    public List<String> getList();
}
