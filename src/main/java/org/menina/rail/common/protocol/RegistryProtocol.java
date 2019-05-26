package org.menina.rail.common.protocol;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.proxy.Exporter;
import org.menina.rail.common.proxy.Invoker;

/**
 * @author zhenghao
 * @date 2019/1/12
 *
 * service discovery not support for now
 */
public class RegistryProtocol implements Protocol {

    @Override
    public <T> Invoker<T> refer(Class<T> interfaces, RpcContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        throw new UnsupportedOperationException();
    }
}
