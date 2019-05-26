package org.menina.rail.transpot.exchange;

import com.google.common.base.Preconditions;
import org.menina.rail.client.Client;
import org.menina.rail.common.NamedThreadFactory;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.common.task.HeartbeatTask;
import org.menina.rail.transpot.Channel;
import org.menina.rail.transpot.DefaultFuture;
import org.menina.rail.common.task.ReconnectTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
@Slf4j
public class HeaderExchangeClient implements ExchangeClient<Invocation> {

    private Client client;
    private ScheduledExecutorService heartBeater;
    private ScheduledExecutorService reconnector;

    public HeaderExchangeClient(Client client, boolean enableHeartbeat) {
        Preconditions.checkNotNull(client);
        this.client = client;
        if (enableHeartbeat) {
            this.heartBeater = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("heartbeat-schedule-thread"));
            this.reconnector = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("reconnect-schedule-thread"));
            HeartbeatTask heartbeatTask = new HeartbeatTask(client);
            ReconnectTask reconnectTask = new ReconnectTask(client);
            this.heartBeater.scheduleAtFixedRate(heartbeatTask,
                    client.getOptions().getHeartbeatTimeoutMills(),
                    client.getOptions().getHeartbeatTimeoutMills(),
                    TimeUnit.MILLISECONDS);
            this.reconnector.scheduleAtFixedRate(reconnectTask,
                    client.getOptions().getReconnectCheckIntervalMills(),
                    client.getOptions().getReconnectCheckIntervalMills(),
                    TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public CompletableFuture request(Invocation invocation) throws RemoteException {
        return this.request(invocation, 0);
    }

    @Override
    public CompletableFuture request(Invocation invocation, int timeout) throws RemoteException {
        this.client.getChannel().send(RpcUtils.newRequest(invocation));
        return DefaultFuture.newFuture(invocation, timeout);
    }

    @Override
    public Channel getChannel() throws RemoteException {
        return this.client.getChannel();
    }

    @Override
    public void close() {
        this.heartBeater.shutdown();
        this.reconnector.shutdown();
        this.client.close();
    }
}
