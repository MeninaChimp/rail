package org.menina.rail.common.thread;

import org.menina.rail.common.NamedThreadFactory;
import org.menina.rail.common.RpcContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public class FixedThreadPool implements ThreadPool {

    @Override
    public ExecutorService getExecutors(RpcContext context) {
        return new ThreadPoolExecutor(context.getOptions().getThreadPoolSize(),
                context.getOptions().getThreadPoolSize(),
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("server-process-thread"));
    }
}
