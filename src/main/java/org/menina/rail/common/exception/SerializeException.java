package org.menina.rail.common.exception;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
public class SerializeException extends RemoteException {

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
