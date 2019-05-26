package org.menina.rail.test.server;

import org.menina.rail.common.annotation.Exporter;
import org.menina.rail.test.api.MockService;
import org.menina.rail.test.api.SetMessage;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/8
 */
@Exporter
public class MockServiceImpl implements MockService {
    @Override
    public CompletableFuture<SetMessage.Response> mock(SetMessage.Request request) {
        System.out.println(request.getMessage());
        if (request.getId() == 1) {
            throw new RuntimeException("make exception");
        }

        return CompletableFuture.completedFuture(SetMessage.Response.newBuilder()
                .setId(2)
                .setMessage("echo from server")
                .build());
    }
}
