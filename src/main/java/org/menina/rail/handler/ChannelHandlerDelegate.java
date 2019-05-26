package org.menina.rail.handler;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public interface ChannelHandlerDelegate<T> extends ChannelHandler<T> {

    ChannelHandler getChannelHandler();
}
