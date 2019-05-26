package org.menina.rail.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.protobuf.MessageLite;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.common.annotation.Exporter;
import org.menina.rail.common.protocol.Protocol;
import org.menina.rail.common.protocol.ProtocolFilterWrapper;
import org.menina.rail.common.protocol.RpcProtocol;
import org.menina.rail.common.proxy.Invoker;
import org.menina.rail.common.proxy.JdkProxyFactory;
import org.menina.rail.common.proxy.ProxyFactory;
import org.menina.rail.config.ServerOptions;
import org.menina.rail.server.export.Export;
import org.menina.rail.server.export.ServiceInfo;
import org.menina.rail.server.export.ServiceRegister;
import org.menina.rail.server.scan.ReflectionScanner;
import org.menina.rail.transpot.netty4.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author zhenghao
 * @date 2019/2/7
 */
@Slf4j
public class ExporterServer implements Server, Export {

    private Server wrapper;
    private Protocol protocol;
    private RpcContext context;
    private ReflectionScanner scanner;
    private ServerOptions config;
    private final Lock startLock = new ReentrantLock();
    private final ProxyFactory proxyFactory = new JdkProxyFactory();
    private Map<String, Invoker<?>> invokers = Maps.newHashMap();
    private ConcurrentMap<String, Object> exporters = new ConcurrentHashMap<>();

    public ExporterServer(ServerOptions config) {
        Preconditions.checkNotNull(config);
        this.config = config;
        this.scanner = new ReflectionScanner(config.getBasePackage());
        this.context = RpcContext.builder().options(config).build();
        this.protocol = new ProtocolFilterWrapper(new RpcProtocol(), context);
        this.wrapper = new NettyServer(config);
    }

    @Override
    public void start() {
        startLock.lock();
        try {
            if (this.wrapper.running()) {
                return;
            }

            Set<Class<?>> classes = scanner.findTypeByAnnotation(Exporter.class);
            Set<Method> methods = scanner.findMethodByAnnotation(Exporter.class);
            classes.iterator().forEachRemaining(new Consumer<Class<?>>() {
                @Override
                public void accept(Class<?> clazz) {
                    methods.addAll(scanner.getAllMethods(clazz));
                }
            });

            try {
                for (Method method : methods) {
                    if (method.getParameterTypes().length == 0) {
                        log.info("solve no args request type will be support later, method info {}", method.getName());
                        continue;
                    }

                    Class<?> requestType = method.getParameterTypes()[0];
                    if (!MessageLite.class.isAssignableFrom(requestType)) {
                        throw new IllegalArgumentException(requestType.toString() + " illegal, proto message only support, method info " + method.getName());
                    }

                    Class<?> clazz = method.getDeclaringClass();
                    if (clazz.isInterface()) {
                        continue;
                    }

                    if (!exporters.containsKey(clazz.getName())) {
                        throw new IllegalStateException("service provider " + clazz.getName() + " not found, need export.");
                    }

                    if (!invokers.containsKey(clazz.getName())) {
                        invokers.put(clazz.getName(), proxyFactory.getInvoker(exporters.get(clazz.getName()), method, context));
                    }

                    ServiceInfo info = ServiceInfo.builder()
                            .method(method)
                            .exporter(protocol.export(invokers.get(clazz.getName())))
                            .returnType(method.getReturnType())
                            .requestType(requestType)
                            .build();

                    Exporter.Meta meta = method.getAnnotation(Exporter.Meta.class);
                    String serviceName = meta == null ? clazz.getInterfaces()[0].getName() : meta.serviceName();
                    String methodName = meta == null ? method.getName() : meta.methodName();
                    String signature = RpcUtils.buildServiceKey(config.getPort(), serviceName, methodName);
                    ServiceRegister.instance().export(signature, info);
                }

                wrapper.start();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            startLock.unlock();
        }
    }

    @Override
    public void close() {
        this.wrapper.close();
    }

    @Override
    public boolean running() {
        return this.wrapper.running();
    }

    @Override
    public CompletableFuture closeFuture() {
        return this.wrapper.closeFuture();
    }

    @Override
    public void export(Object provider) {
        Preconditions.checkNotNull(provider);
        Preconditions.checkArgument(!provider.getClass().isInterface());
        Preconditions.checkArgument(provider.getClass().getInterfaces().length > 0, "the service provider must be an interface implementation");
        exporters.put(provider.getClass().getName(), provider);
    }
}
