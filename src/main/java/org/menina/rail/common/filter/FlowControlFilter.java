package org.menina.rail.common.filter;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import org.menina.rail.common.annotation.ActiveFilter;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.common.Scope;
import org.menina.rail.common.proxy.Invoker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
@ActiveFilter(scope = Scope.CONSUMER)
public class FlowControlFilter implements Filter {

    private RateLimiter rateLimiter;

    private AtomicBoolean available = new AtomicBoolean(false);

    private AtomicBoolean preparing = new AtomicBoolean(false);

    @Override
    public CompletableFuture invoke(Invoker invoker, Invocation invocation) throws RpcException {
        if (!invoker.getContext().getOptions().isTpsLimitEnable()) {
            return invoker.invoke(invocation);
        }

        Double limit = invoker.getContext().getOptions().getTpsLimit();
        Preconditions.checkNotNull(limit);
        if (available.get()) {
            if (!rateLimiter.tryAcquire(1)) {
                throw new RpcException("Request frequency exceeds tps limit： " + limit);
            }
        } else {
            if (preparing.compareAndSet(false, true)) {
                rateLimiter = RateLimiter.create(limit);
                available.set(true);
            }
            // trade off
        }

        return invoker.invoke(invocation);
    }
}
