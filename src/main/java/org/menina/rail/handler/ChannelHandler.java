package org.menina.rail.handler;

import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public interface ChannelHandler<T> {

    void channelActive(Channel channel) throws RemoteException;

    void channelInactive(Channel channel) throws RemoteException;

    void receive(Channel channel, T message) throws RemoteException;

    void caught(Channel channel, Throwable t) throws RemoteException;

    void userEventTriggered(Channel channel, Object event);
}
