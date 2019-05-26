package org.menina.rail.common.protocol;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.proxy.Exporter;
import org.menina.rail.common.proxy.RpcExporter;
import org.menina.rail.transpot.exchange.DefaultExchanger;
import org.menina.rail.transpot.exchange.Exchanger;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Invoker;
import org.menina.rail.common.proxy.RpcInvoker;
import org.menina.rail.config.BaseOptions;
import org.menina.rail.config.ClientOptions;
import org.menina.rail.transpot.exchange.ExchangeClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public class RpcProtocol implements Protocol {

    private static final ConcurrentMap<String, ExchangeClient> SHARD_CONNECTIONS = new ConcurrentHashMap<>();
    private static final Lock LOCK = new ReentrantLock();
    private Exchanger exchanger = new DefaultExchanger();

    @Override
    public <T> Invoker<T> refer(Class<T> interfaces, RpcContext context) throws RemoteException {
        Preconditions.checkArgument(interfaces.isInterface());
        return new RpcInvoker<T>(interfaces, context, this.getClients(context));
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        return new RpcExporter<T>(invoker);
    }

    private ExchangeClient[] getClients(RpcContext context) throws RemoteException {
        Preconditions.checkArgument(BaseOptions.Side.CLIENT.equals(context.getOptions().currentSide()));
        ClientOptions options = (ClientOptions) context.getOptions();
        if (options.isShareConnection()) {
            String remoteAddress = Joiner.on(":").join(options.getRemoteAddress(), options.getPort());
            if (SHARD_CONNECTIONS.containsKey(remoteAddress)) {
                return new ExchangeClient[]{SHARD_CONNECTIONS.get(remoteAddress)};
            } else {
                LOCK.lock();
                try {
                    if (!SHARD_CONNECTIONS.containsKey(remoteAddress)) {
                        ExchangeClient newClient = exchanger.connect(context);
                        SHARD_CONNECTIONS.put(remoteAddress, newClient);
                    }
                } finally {
                    LOCK.unlock();
                }

                return new ExchangeClient[]{SHARD_CONNECTIONS.get(remoteAddress)};
            }
        } else {
            ExchangeClient[] newClients = new ExchangeClient[options.getConnections()];
            for (int i = 0; i < options.getConnections(); i++) {
                newClients[i] = exchanger.connect(context);
            }

            return newClients;
        }
    }
}
