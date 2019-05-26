package org.menina.rail.common.exception;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
public class ChannelClosedException extends RemoteException {
    public ChannelClosedException(String message) {
        super(message);
    }

    public ChannelClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
