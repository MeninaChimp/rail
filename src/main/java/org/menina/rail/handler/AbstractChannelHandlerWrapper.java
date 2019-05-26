package org.menina.rail.handler;

import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AbstractChannelHandlerWrapper<T> implements ChannelHandlerDelegate<T> {

    protected ChannelHandler handler;

    public AbstractChannelHandlerWrapper(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return this.handler;
    }

    @Override
    public void channelActive(Channel channel) throws RemoteException {
        this.handler.channelActive(channel);
    }

    @Override
    public void channelInactive(Channel channel) throws RemoteException {
        this.handler.channelInactive(channel);
    }

    @Override
    public void receive(Channel channel, T message) throws RemoteException {
        this.handler.receive(channel, message);
    }

    @Override
    public void caught(Channel channel, Throwable t) throws RemoteException {
        if (t instanceof IOException) {
            return;
        }

        this.handler.caught(channel, t);
    }

    @Override
    public void userEventTriggered(Channel channel, Object event) {
        this.handler.userEventTriggered(channel, event);
    }
}
