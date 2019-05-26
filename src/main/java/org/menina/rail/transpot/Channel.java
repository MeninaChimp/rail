package org.menina.rail.transpot;

import org.menina.rail.common.Closeable;
import org.menina.rail.common.exception.RemoteException;

import java.net.InetSocketAddress;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public interface Channel extends Closeable {

    InetSocketAddress getRemoteAddress();

    InetSocketAddress getLocalAddress();

    boolean isClosed();

    boolean isConnected();

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

    Object removeAttribute(String key);

    boolean hasAttribute(String key);

    void clearAttribute();

    void send(Object message) throws RemoteException;
}
