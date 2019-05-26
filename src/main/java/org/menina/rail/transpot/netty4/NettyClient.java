package org.menina.rail.transpot.netty4;

import com.google.common.base.Joiner;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.client.Client;
import org.menina.rail.common.NamedThreadFactory;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.config.ClientOptions;
import org.menina.rail.protocol.codec.RpcClientCodec;
import org.menina.rail.transpot.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
@Slf4j
public class NettyClient implements Client {

    private ClientOptions config;

    private Bootstrap bootstrap;

    private NettyChannel channel;

    private static final ConcurrentMap<io.netty.channel.Channel, Client> ALL_CHANNELS = new ConcurrentHashMap<>();

    public NettyClient(ClientOptions config) {
        NettyClient.this.config = config;
        NettyClient.this.bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMills())
                .option(ChannelOption.SO_KEEPALIVE, config.isKeepalive())
                .option(ChannelOption.SO_LINGER, config.getSoLinger())
                .option(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, config.getReceiveBufferSize())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.group(new NioEventLoopGroup(config.getIoThreadNum(), new NamedThreadFactory("client-io-thread")))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RpcClientCodec())
                                .addLast(new NettyClientHandler());
                    }
                });

    }

    @Override
    public void connect(boolean reconnect) throws RemoteException {
        ChannelFuture future = bootstrap.connect(config.getRemoteAddress(), config.getPort());
        if (!future.awaitUninterruptibly(config.getConnectTimeoutMills())) {
            throw new RemoteException("Connect timeout to remote address " + Joiner.on(":").join(config.getRemoteAddress(), config.getPort()));
        }

        if (!future.isSuccess()) {
            if (!reconnect && config.isLazyConnect()) {
                this.channel = new NettyChannel(future.channel());
                return;
            }

            throw new RemoteException(future.cause().getMessage());
        }

        io.netty.channel.Channel channel = future.channel();
        this.channel = new NettyChannel(channel);
        if (config.getConnectStateListener() != null) {
            config.getConnectStateListener().onConnected(config.getRemoteAddress(), config.getPort());
        }

        ALL_CHANNELS.put(channel, this);
    }

    @Override
    public void reconnect() throws RemoteException {
        try {
            connect(true);
            log.info("Reconnect to {}:{} success, channel info {}", config.getRemoteAddress(), config.getPort(), this.channel);
        } catch (RemoteException e) {
            log.warn("Reconnect to {}:{} failed", config.getRemoteAddress(), config.getPort());
            throw e;
        }
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ClientOptions getOptions() {
        return this.config;
    }

    @Override
    public void close() {
        if (config.getConnectStateListener() != null) {
            config.getConnectStateListener().onDisconnected(config.getRemoteAddress(), config.getPort());
        }

        this.channel.close();
    }

    public static void close(io.netty.channel.Channel channel) {
        ALL_CHANNELS.remove(channel).close();
    }
}
