package org.menina.rail.transpot.netty4;

import com.google.protobuf.MessageLite;
import org.menina.rail.common.exception.RpcException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.transpot.DefaultFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage<RpcHeader.ResponseHeader>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcHeader.ResponseHeader> response) throws Exception {
        DefaultFuture.DecodeFuture future = DefaultFuture.remove(response.getHeader().getRequestId());
        if (future == null) {
            return;
        }

        Invocation invocation = future.getInvocation();
        if (response.getHeader().getResponseCode().equals(RpcHeader.ResponseCode.SUCCESS)) {
            try {
                if (response.getBody() == null) {
                    future.getResult().complete(null);
                } else {
                    Method decodeMethod = invocation.getResponseType().getMethod("parseFrom", byte[].class);
                    MessageLite responseBody = (MessageLite) decodeMethod.invoke(null, (Object) response.getBody());
                    future.getResult().complete(responseBody);
                }
            } catch (Exception e) {
                future.getResult().completeExceptionally(e);
                throw e;
            }
        } else {
            future.getResult().completeExceptionally(new RpcException(response.getHeader().getResponseMessage()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connected from {} --> {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.close(ctx.channel());
        log.info("DisConnected from {} --> {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if ((cause instanceof IOException)) {
            return;
        }

        if (cause instanceof RpcException) {
            log.error(cause.getMessage());
            return;
        }

        log.error(cause.getMessage(), cause);
    }
}
