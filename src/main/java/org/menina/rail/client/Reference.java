package org.menina.rail.client;

import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.protocol.Protocol;
import org.menina.rail.common.protocol.RpcProtocol;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.common.protocol.ProtocolFilterWrapper;
import org.menina.rail.common.proxy.Invoker;
import org.menina.rail.common.proxy.JdkProxyFactory;
import org.menina.rail.common.proxy.ProxyFactory;
import org.menina.rail.config.ClientOptions;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reference {

    private ClientOptions config;

    private ProxyFactory proxyFactory;

    public Reference(ClientOptions config) {
        Preconditions.checkNotNull(config);
        this.config = config;
        this.proxyFactory = new JdkProxyFactory();
    }

    public <T> T refer(Class<T> type) {
        Preconditions.checkArgument(type.isInterface());
        RpcContext context = RpcContext.builder().options(config).build();
        Protocol protocol = new ProtocolFilterWrapper(new RpcProtocol(), context);
        Invoker<T> invoker = null;
        try {
            invoker = protocol.refer(type, context);
        } catch (RemoteException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return proxyFactory.getProxy(invoker);
    }
}
