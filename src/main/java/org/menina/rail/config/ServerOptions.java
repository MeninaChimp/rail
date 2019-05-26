package org.menina.rail.config;

import org.menina.rail.common.NetUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.net.InetAddress;

/**
 * @author zhenghao
 * @date 2018/12/13
 */
@Data
@Builder
public class ServerOptions extends BaseOptions{

    @NonNull
    private int port;

    @Builder.Default
    private InetAddress address = NetUtils.getLocalAddress();

    @Builder.Default
    private int acceptorThreadNum = 1;

    @Builder.Default
    private int ioThreadNum = Runtime.getRuntime().availableProcessors();

    @Builder.Default
    private int backlogSize = 512;

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

    @Override
    public Side currentSide() {
        return Side.SERVER;
    }

    @Override
    public int getPort() {
        return port;
    }
}
