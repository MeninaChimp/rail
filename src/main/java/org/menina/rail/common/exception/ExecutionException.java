package org.menina.rail.common.exception;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public class ExecutionException extends RemoteException {
    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
