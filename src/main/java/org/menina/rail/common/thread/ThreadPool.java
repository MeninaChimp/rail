package org.menina.rail.common.thread;

import org.menina.rail.common.RpcContext;

import java.util.concurrent.ExecutorService;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public interface ThreadPool {

    ExecutorService getExecutors(RpcContext context);
}
