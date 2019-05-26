package org.menina.rail.transpot;

import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.exception.ChannelClosedException;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public abstract class AbstractChannel implements Channel {

    protected volatile boolean close;

    @Override
    public void send(Object message) throws RemoteException {
        if (!isConnected()) {
            throw new ChannelClosedException("Failed to send message to " + this.getRemoteAddress() + ", cause channel is not connected");
        }
    }
}
