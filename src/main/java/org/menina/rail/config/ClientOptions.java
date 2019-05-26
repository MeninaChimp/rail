package org.menina.rail.config;

import org.menina.rail.client.ConnectStateListener;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
@Data
@Builder
public class ClientOptions extends BaseOptions {

    @NonNull
    private int port;

    @NonNull
    private String remoteAddress;

    private boolean lazyConnect;

    private ConnectStateListener connectStateListener;

    @Builder.Default
    private int retries = 3;

    @Builder.Default
    private int connectTimeoutMills = 3 * 1000;

    @Builder.Default
    private int readTimeoutMills = 5 * 1000;

    @Builder.Default
    private int soLinger = 4;

    @Builder.Default
    private boolean keepalive = true;

    @Builder.Default
    private boolean tcpNoDelay = true;

    @Builder.Default
    private int receiveBufferSize = 64 * 1024;

    @Builder.Default
    private int sendBufferSize = 64 * 1024;

    @Builder.Default
    private int heartbeatTimeoutMills = 3 * 1000;

    @Builder.Default
    private int reconnectCheckIntervalMills = 15 * 1000;

    @Builder.Default
    private int ioThreadNum = Runtime.getRuntime().availableProcessors();

    @Override
    public Side currentSide() {
        return Side.CLIENT;
    }

    @Override
    public int getPort() {
        return port;
    }
}
