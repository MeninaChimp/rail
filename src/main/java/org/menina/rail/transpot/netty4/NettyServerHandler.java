package org.menina.rail.transpot.netty4;

import com.google.common.base.Preconditions;
import org.menina.rail.protocol.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.handler.ChannelHandler;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.transpot.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhenghao
 * @date 2019/1/7
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcMessage<RpcHeader.RequestHeader>> {

    private ConcurrentMap<io.netty.channel.Channel, Channel> channels = new ConcurrentHashMap<>();
    private ChannelHandler handler;

    public NettyServerHandler(ChannelHandler handler){
        Preconditions.checkNotNull(handler);
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcHeader.RequestHeader> msg) throws Exception {
        NettyChannel channel = (NettyChannel) this.channels.getOrDefault(ctx.channel(), null);
        Preconditions.checkNotNull(channel);
        this.handler.receive(channel, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel channel = (NettyChannel) this.channels.getOrDefault(ctx.channel(), null);
        Preconditions.checkNotNull(channel);
        this.handler.caught(channel, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = new NettyChannel(ctx.channel());
        this.channels.put(ctx.channel(), channel);
        this.handler.channelActive(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = (NettyChannel) this.channels.remove(ctx.channel());
        this.handler.channelInactive(channel);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        NettyChannel channel = (NettyChannel) this.channels.getOrDefault(ctx.channel(), null);
        this.handler.userEventTriggered(channel, evt);
    }
}
