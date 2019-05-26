package org.menina.rail.common.proxy;

import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RpcException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.exchange.ExchangeClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcInvoker<T> implements Invoker<T> {

    private Class<T> serviceType;

    private ExchangeClient[] clients;

    private RpcContext context;

    private AtomicInteger index = new AtomicInteger(0);

    public RpcInvoker(Class<T> serviceType, RpcContext context, ExchangeClient[] clients) throws RemoteException {
        Preconditions.checkNotNull(serviceType);
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(clients);
        Preconditions.checkArgument(clients.length > 0);
        this.serviceType = serviceType;
        this.context = context;
        this.clients = clients;
    }

    @Override
    public RpcContext getContext() {
        return context;
    }

    @Override
    public Class<T> getInterface() {
        return serviceType;
    }

    @Override
    public CompletableFuture invoke(Invocation invocation) throws RpcException {
        ExchangeClient client;
        if (clients.length == 1) {
            client = clients[0];
        } else {
            client = clients[(index.getAndIncrement() & Integer.MAX_VALUE) % clients.length];
        }

        try {
            return client.request(invocation);
        } catch (RemoteException e) {
            throw new RpcException(e.getMessage(), e);
        }
    }
}
