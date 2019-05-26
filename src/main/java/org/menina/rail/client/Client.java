package org.menina.rail.client;

import org.menina.rail.common.Closeable;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;
import org.menina.rail.config.ClientOptions;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public interface Client extends Closeable {

    ClientOptions getOptions();

    Channel getChannel();

    void connect(boolean reconnect) throws RemoteException;

    void reconnect() throws RemoteException;

}
