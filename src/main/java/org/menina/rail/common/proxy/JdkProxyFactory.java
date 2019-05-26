package org.menina.rail.common.proxy;

import com.google.common.base.Preconditions;
import com.google.protobuf.MessageLite;
import org.menina.rail.common.IdGenerator;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.annotation.Exporter;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.RpcConstants;
import org.menina.rail.common.RpcUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{invoker.getInterface()},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Preconditions.checkArgument(args.length == 1);
                        Preconditions.checkArgument(MessageLite.class.isAssignableFrom(method.getParameterTypes()[0]));
                        Class<?> responseType;
                        if (method.getReturnType().isAssignableFrom(CompletableFuture.class)) {
                            responseType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        } else {
                            responseType = method.getReturnType();
                        }

                        Preconditions.checkArgument(MessageLite.class.isAssignableFrom(responseType));
                        Exporter.Meta meta = method.getAnnotation(Exporter.Meta.class);
                        Invocation invocation = Invocation.builder()
                                .arguments(args)
                                .methodName(meta == null ? method.getName() : meta.methodName())
                                .responseType(responseType)
                                .parametersType(method.getParameterTypes())
                                .requestId(IdGenerator.get().nextId())
                                .build()
                                .addAttachment(RpcConstants.PATH_KEY, meta == null ? invoker.getInterface().getName() : meta.serviceName())
                                .addAttachment(RpcConstants.VERSION_KET, RpcConstants.CURRENT_VERSION)
                                .addAttachment(RpcConstants.RETRY_KET, String.valueOf(invoker.getContext().getOptions().getRetries()))
                                .addAttachment(RpcConstants.SERIALIZER_KEY, invoker.getContext().getOptions().getSerializer().name());

                        return invoker.invoke(invocation);
                    }
                });
    }

    @Override
    public <T> Invoker<T> getInvoker(T ref, Method method, RpcContext context) {
        Preconditions.checkNotNull(ref);
        Preconditions.checkNotNull(method);
        Preconditions.checkNotNull(context);
        return new Invoker<T>() {
            @Override
            public RpcContext getContext() {
                return context;
            }

            @Override
            public Class<T> getInterface() {
                return (Class<T>) ref.getClass().getInterfaces()[0];
            }

            @Override
            public CompletableFuture invoke(Invocation invocation) throws RpcException {
                try {
                    Object result = method.invoke(ref, invocation.getArguments());
                    if (result instanceof CompletableFuture) {
                        return (CompletableFuture) result;
                    } else {
                        return CompletableFuture.completedFuture(result);
                    }
                } catch (Throwable t) {
                    Throwable targetException = t instanceof InvocationTargetException ? ((InvocationTargetException) t).getTargetException() : t;
                    throw new RpcException(RpcUtils.stackTrace(targetException), targetException);
                }
            }
        };
    }
}
