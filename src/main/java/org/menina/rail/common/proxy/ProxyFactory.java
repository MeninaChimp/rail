package org.menina.rail.common.proxy;

import org.menina.rail.common.RpcContext;

import java.lang.reflect.Method;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public interface ProxyFactory {

    <T> T getProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T ref, Method method, RpcContext context);

}
