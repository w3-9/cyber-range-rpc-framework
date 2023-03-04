package org.young.irpc.framework.core.common.exception.impl;

import org.young.irpc.framework.core.common.exception.IRpcException;

/**
 * @ClassName RpcTokenNotVerifiedException
 * @Description TODO
 * @Author young
 * @Date 2023/3/2 下午3:34
 * @Version 1.0
 **/
public class RpcTokenNotVerifiedException extends IRpcException {
    private static final long serialVersionUID = -7040331868899109336L;

    public RpcTokenNotVerifiedException(String s) {
        super(s);
    }
}
