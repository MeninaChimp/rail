package org.menina.rail.server.dispatch;

import org.menina.rail.common.RpcContext;
import org.menina.rail.handler.ChannelHandler;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public interface Dispatch {

    ChannelHandler dispatch(ChannelHandler handler, RpcContext context);
}
