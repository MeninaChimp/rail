package org.menina.rail.handler;

import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.thread.FixedThreadPool;
import org.menina.rail.common.thread.ThreadPool;

import java.util.concurrent.ExecutorService;


/**
 * @author zhenghao
 * @date 2019/1/14
 */
public abstract class ConcurrentChannelHandlerWrapper<T> extends ContextAwareChannelHandlerWrapper<T> {

    private ThreadPool threadPool = new FixedThreadPool();
    protected ExecutorService executors;

    public ConcurrentChannelHandlerWrapper(ChannelHandler handler, RpcContext context) {
        super(handler, context);
        this.executors = threadPool.getExecutors(context);
    }

    public ConcurrentChannelHandlerWrapper(ChannelHandler handler, RpcContext context, ExecutorService executors) {
        super(handler, context);
        Preconditions.checkNotNull(executors);
        this.executors = executors;
    }
}
