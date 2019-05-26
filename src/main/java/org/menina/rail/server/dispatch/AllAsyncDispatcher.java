package org.menina.rail.server.dispatch;

import org.menina.rail.common.RpcContext;
import org.menina.rail.handler.AllChannelHandler;
import org.menina.rail.handler.ChannelHandler;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public class AllAsyncDispatcher implements Dispatch {

    @Override
    public ChannelHandler dispatch(ChannelHandler handler, RpcContext context) {
        return new AllChannelHandler(handler, context);
    }
}
