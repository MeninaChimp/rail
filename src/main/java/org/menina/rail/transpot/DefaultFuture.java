package org.menina.rail.transpot;

import org.menina.rail.common.proxy.Invocation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
public class DefaultFuture {

    @Data
    @AllArgsConstructor
    public static class DecodeFuture {

        private CompletableFuture<Object> result;

        private Invocation invocation;
    }

    private static ConcurrentMap<Long, DecodeFuture> pending = new ConcurrentHashMap<>();

    public static CompletableFuture newFuture(Invocation invocation, long timeout) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pending.put(invocation.getRequestId(), new DecodeFuture(future, invocation));
        return future;
    }

    public static DecodeFuture remove(Long requestId){
        return pending.remove(requestId);
    }
}
