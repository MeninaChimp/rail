package org.menina.rail.transpot.netty4;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.AbstractChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
@Slf4j
public class NettyChannel extends AbstractChannel {

    private io.netty.channel.Channel channel;
    private ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public boolean isClosed() {
        return this.close;
    }

    @Override
    public boolean isConnected() {
        return !isClosed() && channel.isActive();
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public void clearAttribute() {
        this.attributes.clear();
    }

    @Override
    public void send(Object message) throws RemoteException {
        super.send(message);
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable t) {
            throw new RemoteException("Failed to send message to " + this.getRemoteAddress() + " , cause " + t.getMessage());
        }
    }

    @Override
    public void close() {
        this.close = true;
        this.channel.close();
        log.info("Close channel {}", this.toString());
    }

    @Override
    public String toString() {
        return "[" + channel.localAddress() + " --> " + channel.remoteAddress() + "]";
    }
}
