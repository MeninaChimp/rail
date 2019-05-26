package org.menina.rail.common.proxy;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RpcException;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public interface Invoker<T> {

    RpcContext getContext();

    Class<T> getInterface();

    CompletableFuture invoke(Invocation invocation) throws RpcException;
}
