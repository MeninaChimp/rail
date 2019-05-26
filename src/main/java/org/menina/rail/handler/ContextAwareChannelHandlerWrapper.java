package org.menina.rail.handler;

import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
public abstract class ContextAwareChannelHandlerWrapper<T> extends AbstractChannelHandlerWrapper<T> {

    protected RpcContext context;

    public ContextAwareChannelHandlerWrapper(ChannelHandler handler, RpcContext context) {
        super(handler);
        Preconditions.checkNotNull(context);
        this.context = context;
    }

    /**
     * rpc context aware
     * @return RpcContext
     */
    public abstract RpcContext context();
}
