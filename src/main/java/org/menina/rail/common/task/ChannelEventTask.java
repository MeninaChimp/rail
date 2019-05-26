package org.menina.rail.common.task;

import com.google.common.base.Preconditions;
import org.menina.rail.common.exception.RemoteException;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.handler.ChannelHandler;
import org.menina.rail.transpot.Channel;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
@Slf4j
public class ChannelEventTask<T> implements Runnable {

    private ChannelHandler handler;

    private Channel channel;

    private EventType eventType;

    private T message;

    public ChannelEventTask(ChannelHandler handler, Channel channel, EventType eventType) {
        Preconditions.checkNotNull(eventType);
        Preconditions.checkNotNull(channel);
        Preconditions.checkNotNull(handler);
        this.handler = handler;
        this.channel = channel;
        this.eventType = eventType;
    }

    public ChannelEventTask(ChannelHandler handler, Channel channel, EventType eventType, T message) {
        Preconditions.checkNotNull(eventType);
        Preconditions.checkNotNull(channel);
        Preconditions.checkNotNull(handler);
        Preconditions.checkNotNull(message);
        this.handler = handler;
        this.channel = channel;
        this.eventType = eventType;
        this.message = message;
    }

    @Override
    public void run() {
        switch (this.eventType) {
            case RECEIVE:
                try {
                    handler.receive(channel, message);
                } catch (RemoteException e) {
                    try {
                        handler.caught(channel, e);
                    } catch (Throwable t) {
                        log.error(e.getMessage(), e);
                    }
                }

                break;
            case CONNECTED:
                try {
                    handler.channelActive(channel);
                } catch (RemoteException e) {
                    log.error(e.getMessage(), e);
                }

                break;
            case DISCONNECTED:
                try {
                    handler.channelInactive(channel);
                } catch (RemoteException e) {
                    log.error(e.getMessage(), e);
                }

                break;
            case EXCEPTION:
                try {
                    handler.caught(channel, (Throwable) message);
                } catch (RemoteException e) {
                    log.error(e.getMessage(), e);
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown channel event type: " + eventType.toString());
        }
    }

    public enum EventType {
        /**
         * New connection
         */
        CONNECTED,

        /**
         * Connection loss
         */
        DISCONNECTED,

        /**
         * Receive message
         */
        RECEIVE,

        /**
         * Error occur
         */
        EXCEPTION
    }
}
