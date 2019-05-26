package org.menina.rail.common.exception;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public class RpcException extends Exception {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
