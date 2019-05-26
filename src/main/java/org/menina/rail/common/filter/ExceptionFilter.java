package org.menina.rail.common.filter;

import org.menina.rail.common.annotation.ActiveFilter;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.common.Scope;
import org.menina.rail.common.proxy.Invoker;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/13
 */
@Slf4j
@ActiveFilter(scope = {Scope.ALL})
public class ExceptionFilter implements Filter {

    @Override
    public CompletableFuture invoke(Invoker invoker, Invocation invocation) throws RpcException {
        try {
            return invoker.invoke(invocation);
        } catch (RuntimeException e) {
            log.error("Get unchecked exception, service {}, method {}, exception message: ",
                    invoker.getInterface().getName(),
                    invocation.getMethodName(),
                    e.getMessage(), e);
            throw e;
        }
    }
}
