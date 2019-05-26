package org.menina.rail.common.exception;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public class RemoteException extends Exception {

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
