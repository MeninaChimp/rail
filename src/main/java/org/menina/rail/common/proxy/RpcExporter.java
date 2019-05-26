package org.menina.rail.common.proxy;

import com.google.common.base.Preconditions;

/**
 * @author zhenghao
 * @date 2019/1/16
 */
public class RpcExporter<T> implements Exporter<T> {

    private Invoker<T> invoker;

    public RpcExporter(Invoker<T> invoker) {
        Preconditions.checkNotNull(invoker);
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }
}
