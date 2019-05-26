package org.menina.rail.transpot.exchange;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RemoteException;

/**
 * @author zhenghao
 * @date 2019/1/16
 */
public interface Exchanger {

    ExchangeClient connect(RpcContext context) throws RemoteException;
}
