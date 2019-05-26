package org.menina.rail.transpot.netty4;

import com.google.common.base.Preconditions;
import org.menina.rail.common.NamedThreadFactory;
import org.menina.rail.common.RpcContext;
import org.menina.rail.config.ServerOptions;
import org.menina.rail.handler.DecodeHandler;
import org.menina.rail.handler.HeartbeatHandler;
import org.menina.rail.handler.IdleChannelHandler;
import org.menina.rail.protocol.codec.RpcServerCodec;
import org.menina.rail.server.ChannelHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.handler.AllChannelHandler;
import org.menina.rail.handler.ExceptionChannelHandler;
import org.menina.rail.handler.ChannelHandler;
import org.menina.rail.handler.HeaderExchangeHandler;
import org.menina.rail.server.Server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhenghao
 * @date 2018/12/13
 */
@Slf4j
public class NettyServer implements Server, ChannelHandlerInitializer {

    private ServerOptions config;

    private ServerBootstrap server;

    private EventLoopGroup bossLoopGroup;

    private EventLoopGroup workerLoopGroup;

    private NettyServerHandler serverHandler;

    private boolean running;

    private RpcContext context;

    private CompletableFuture<Boolean> closeFuture = new CompletableFuture<>();

    public NettyServer(ServerOptions config) {
        Preconditions.checkNotNull(config);
        this.config = config;
        this.server = new ServerBootstrap();
        this.context = RpcContext.builder().options(this.config).build();
        this.serverHandler = new NettyServerHandler(initChannelHandler());
        if (Epoll.isAvailable()) {
            bossLoopGroup = new EpollEventLoopGroup(config.getAcceptorThreadNum(),
                    new NamedThreadFactory("server-acceptor-thread"));
            workerLoopGroup = new EpollEventLoopGroup(config.getIoThreadNum(),
                    new NamedThreadFactory("server-io-thread"));
            server.channel(EpollServerSocketChannel.class);
            ((EpollEventLoopGroup) bossLoopGroup).setIoRatio(100);
            ((EpollEventLoopGroup) workerLoopGroup).setIoRatio(60);
            log.info("Enable epoll selector");
        } else {
            bossLoopGroup = new NioEventLoopGroup(config.getAcceptorThreadNum(),
                    new NamedThreadFactory("server-acceptor-thread"));
            workerLoopGroup = new NioEventLoopGroup(config.getIoThreadNum(),
                    new NamedThreadFactory("server-io-thread"));
            server.channel(NioServerSocketChannel.class);
            ((NioEventLoopGroup) bossLoopGroup).setIoRatio(100);
            ((NioEventLoopGroup) workerLoopGroup).setIoRatio(60);
            log.info("Enable nio selector");
        }

        server.option(ChannelOption.SO_BACKLOG, config.getBacklogSize())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isKeepalive())
                .childOption(ChannelOption.SO_LINGER, config.getSoLinger())
                .childOption(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .childOption(ChannelOption.SO_RCVBUF, config.getReceiveBufferSize())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        server.group(bossLoopGroup, workerLoopGroup)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(
                                        0,
                                        0,
                                        config.getMaxIdleTimeMills(),
                                        TimeUnit.MILLISECONDS))
                                .addLast(new RpcServerCodec())
                                .addLast(serverHandler);
                    }
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        if (!running) {
            try {
                ChannelFuture f = server.bind(config.getPort()).sync();
                log.info("Export server start on port {}", config.getPort());
                running = true;
                f.channel().closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        if (f.cause() != null) {
                            closeFuture.completeExceptionally(f.cause());
                        } else {
                            closeFuture.complete(true);
                        }
                    }
                });

            } catch (InterruptedException e) {
                log.error("Failed to start server for {}", e.getMessage(), e);
                closeFuture.completeExceptionally(e);
            }
        } else {
            log.info("Server had started");
        }
    }

    @Override
    public void close() {
        running = false;
        bossLoopGroup.shutdownGracefully().syncUninterruptibly();
        workerLoopGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Override
    public boolean running() {
        return this.running;
    }

    @Override
    public CompletableFuture closeFuture() {
        return this.closeFuture;
    }

    @Override
    public ChannelHandler initChannelHandler() {
        return new HeartbeatHandler(new IdleChannelHandler(new AllChannelHandler(new ExceptionChannelHandler(new DecodeHandler(new HeaderExchangeHandler(null, context), context)), context), context));
    }
}
