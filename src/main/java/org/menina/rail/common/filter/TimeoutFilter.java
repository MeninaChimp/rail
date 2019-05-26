package org.menina.rail.common.filter;

import org.menina.rail.common.Scope;
import org.menina.rail.common.annotation.ActiveFilter;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.common.proxy.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
@Slf4j
@ActiveFilter(scope = Scope.PROVIDER)
public class TimeoutFilter implements Filter {
    @Override
    public CompletableFuture invoke(Invoker invoker, Invocation invocation) throws RpcException {

        long begin = System.currentTimeMillis();
        CompletableFuture future = invoker.invoke(invocation);
        long end = System.currentTimeMillis();
        long timeout = invoker.getContext().getOptions().getInvokeTimeoutMills();
        if (end - begin > timeout) {
            log.warn("invoke time out, method: " + invocation.getMethodName() + " arguments: " + Arrays.toString(invocation.getArguments()) + ", invoke elapsed " + (end - begin) + " ms.");
        }

        return future;
    }
}
