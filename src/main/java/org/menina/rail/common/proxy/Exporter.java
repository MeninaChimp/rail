package org.menina.rail.common.proxy;

/**
 * @author zhenghao
 * @date 2019/1/16
 */
public interface Exporter<T> {

    Invoker<T> getInvoker();
}
