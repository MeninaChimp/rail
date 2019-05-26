package org.menina.rail.common.filter;

import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.common.proxy.Invoker;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public interface Filter {

    CompletableFuture invoke(Invoker invoker, Invocation invocation) throws RpcException;
}
