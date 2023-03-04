package org.young.irpc.framework.core.common.exception.impl;

import org.young.irpc.framework.core.common.exception.IRpcException;

/**
 * @ClassName RpcFlowLimitExceedException
 * @Description TODO
 * @Author young
 * @Date 2023/3/3 下午10:19
 * @Version 1.0
 **/
public class RpcFlowLimitExceedException extends IRpcException {
    private static final long serialVersionUID = 674300957456268216L;

    public RpcFlowLimitExceedException(String message) {
        super(message);
    }
}
