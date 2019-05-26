package org.menina.rail.test.server;

import org.menina.rail.config.ServerOptions;
import org.menina.rail.server.ExporterServer;
import org.menina.rail.test.api.MockService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhenghao on 2019/1/8.
 */
@Slf4j
public class MockServer {

    public static void main(String[] args) {

        ServerOptions config = ServerOptions.builder()
                .port(20990)
                .acceptorThreadNum(1)
                .backlogSize(512)
                .build();

        config.setThreadPoolSize(10);
        ExporterServer server = new ExporterServer(config);
        MockService mockService = new MockServiceImpl();
        server.export(mockService);
        server.start();
        try {
            server.closeFuture().get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
