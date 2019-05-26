package org.menina.rail.common;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.protobuf.MessageLite;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.common.filter.Filter;
import org.menina.rail.common.proxy.Invoker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by zhenghao on 2019/1/8.
 */
public class RpcUtils {

    public static String buildServiceKey(Integer port, String serviceName, String methodName) {
        Preconditions.checkNotNull(port);
        Preconditions.checkNotNull(serviceName);
        Preconditions.checkNotNull(methodName);
        // port support multiple server in one jvm
        return Joiner.on(":").join(port, serviceName, methodName);
    }

    public static RpcMessage<RpcHeader.RequestHeader> newRequest(Invocation invocation) {
        byte[] body = ((MessageLite) invocation.getArguments()[0]).toByteArray();
        RpcHeader.RequestHeader header = RpcHeader.RequestHeader.newBuilder()
                .setVersion(invocation.getAttachments().get(RpcConstants.VERSION_KET))
                .setMessageType(RpcHeader.MessageType.REQUEST)
                .setMethodName(invocation.getMethodName())
                .putAllAttachments(invocation.getAttachments())
                .setBodyLength(body.length)
                .setRequestId(invocation.getRequestId())
                .build();
        return new RpcMessage<RpcHeader.RequestHeader>(header, body);
    }

    public static RpcMessage<RpcHeader.RequestHeader> newHeartbeat() {
        RpcHeader.RequestHeader header = RpcHeader.RequestHeader.newBuilder()
                .setMessageType(RpcHeader.MessageType.HEARTBEAT)
                .build();
        return new RpcMessage<RpcHeader.RequestHeader>(header, null);
    }

    public static <T> Invoker<T> buildFilterChain(Invoker<T> invoker, List<Filter> filters) {
        Invoker<T> last = invoker;
        if (!filters.isEmpty()) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                final Filter filter = filters.get(i);
                final Invoker<T> next = last;
                last = new Invoker<T>() {
                    @Override
                    public RpcContext getContext() {
                        return invoker.getContext();
                    }

                    @Override
                    public Class<T> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public CompletableFuture invoke(Invocation invocation) throws RpcException {
                        return filter.invoke(next, invocation);
                    }

                    @Override
                    public String toString() {
                        return invoker.toString();
                    }
                };
            }
        }

        return last;
    }

    public static String stackTrace(Throwable cause){
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.println();
        try {
            cause.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
}
