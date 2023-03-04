package org.young.irpc.framework.core.common.exception;

/**
 * @ClassName RpcException
 * @Description TODO
 * @Author young
 * @Date 2023/3/2 下午3:17
 * @Version 1.0
 **/
public abstract class IRpcException extends RuntimeException{
    private static final long serialVersionUID = 3029183590508727946L;

    public IRpcException(String message) {
        super(message);
    }
}
