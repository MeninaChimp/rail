package org.menina.rail.common.protocol;

import com.google.common.base.Preconditions;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.common.annotation.ActiveFilter;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Exporter;
import org.menina.rail.server.scan.ReflectionScanner;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.common.RpcConstants;
import org.menina.rail.common.filter.Filter;
import org.menina.rail.common.proxy.Invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
@Slf4j
public class ProtocolFilterWrapper implements Protocol {

    private Protocol protocol;

    private ReflectionScanner scanner;

    private RpcContext context;

    public ProtocolFilterWrapper(Protocol protocol, RpcContext context) {
        Preconditions.checkNotNull(protocol);
        Preconditions.checkNotNull(context);
        this.protocol = protocol;
        this.context = context;
        this.scanner = new ReflectionScanner(context.getOptions().getBasePackage());
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, RpcContext context) throws RemoteException {
        return RpcUtils.buildFilterChain(this.protocol.refer(type, context), this.scanFilters(RpcConstants.SCOPE_CONSUMER_ENABLE));
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        return this.protocol.export(RpcUtils.buildFilterChain(invoker, this.scanFilters(RpcConstants.SCOPE_PROVIDER_ENABLE)));
    }

    private List<Filter> scanFilters(byte scope) {
        Set<Class<?>> filterClasses = scanner.findTypeByAnnotation(ActiveFilter.class);
        List<Filter> filterList = new ArrayList<>();
        filterClasses.iterator().forEachRemaining(new Consumer<Class<?>>() {
            @Override
            public void accept(Class<?> clazz) {
                ActiveFilter annotation = clazz.getAnnotation(ActiveFilter.class);
                boolean enable = false;
                for (int i = 0; i < annotation.scope().length; i++) {
                    enable |= (scope & annotation.scope()[i].getValue()) != 0;
                }

                if (enable && Filter.class.isAssignableFrom(clazz)) {
                    try {
                        filterList.add((Filter) clazz.newInstance());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });

        return filterList;
    }
}
