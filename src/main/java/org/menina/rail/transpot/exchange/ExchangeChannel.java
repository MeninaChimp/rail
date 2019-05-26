package org.menina.rail.transpot.exchange;

import org.menina.rail.common.Closeable;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public interface ExchangeChannel<T> extends Closeable {

    CompletableFuture request(T message) throws RemoteException;

    CompletableFuture request(T message, int timeout) throws RemoteException;

    Channel getChannel() throws RemoteException;

}
