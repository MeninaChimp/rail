package org.menina.rail.common.protocol;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.proxy.Exporter;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Invoker;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public interface Protocol {

    <T> Invoker<T> refer(Class<T> type, RpcContext context) throws RemoteException;

    <T> Exporter<T> export(Invoker<T> invoker);

}
