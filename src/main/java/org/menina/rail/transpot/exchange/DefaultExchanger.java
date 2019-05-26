package org.menina.rail.transpot.exchange;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.config.ClientOptions;
import org.menina.rail.transpot.netty4.NettyClient;

/**
 * @author zhenghao
 * @date 2019/1/16
 */
public class DefaultExchanger implements Exchanger {

    @Override
    public ExchangeClient<Invocation> connect(RpcContext context) throws RemoteException {
        NettyClient nettyClient = new NettyClient((ClientOptions) context.getOptions());
        nettyClient.connect(false);
        return new HeaderExchangeClient(nettyClient, true);
    }
}
