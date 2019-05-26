package org.menina.rail.handler;

import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.ExecutionException;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.task.ChannelEventTask;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.transpot.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
@Slf4j
public class AllChannelHandler extends ConcurrentChannelHandlerWrapper<RpcMessage<RpcHeader.RequestHeader>> {

    public AllChannelHandler(ChannelHandler handler, RpcContext context) {
        super(handler, context);
    }

    @Override
    public RpcContext context() {
        return this.context;
    }

    public AllChannelHandler(ChannelHandler handler, RpcContext context, ExecutorService executors) {
        super(handler, context, executors);
    }

    @Override
    public void channelActive(Channel channel) throws RemoteException {
        try {
            this.executors.execute(new ChannelEventTask<Void>(this.handler, channel, ChannelEventTask.EventType.CONNECTED));
        } catch (Throwable t) {
            throw new ExecutionException(t.getMessage(), t);
        }
    }

    @Override
    public void channelInactive(Channel channel) throws RemoteException {
        try {
            this.executors.execute(new ChannelEventTask<Void>(this.handler, channel, ChannelEventTask.EventType.DISCONNECTED));
        } catch (Throwable t) {
            throw new ExecutionException(t.getMessage(), t);
        }
    }

    @Override
    public void receive(Channel channel, RpcMessage<RpcHeader.RequestHeader> request) throws RemoteException {
        try {
            this.executors.execute(new ChannelEventTask<Object>(this.handler, channel, ChannelEventTask.EventType.RECEIVE, request));
        } catch (RejectedExecutionException e) {
            String err = "Thread pool is exhausted, channel: " + channel + ", error message: " + e.getMessage();
            RpcHeader.ResponseHeader header = RpcHeader.ResponseHeader.newBuilder()
                    .setRequestId(request.getHeader().getRequestId())
                    .setResponseCode(RpcHeader.ResponseCode.FAIL)
                    .setResponseMessage(err)
                    .build();
            log.error(err);
            channel.send(new RpcMessage<RpcHeader.ResponseHeader>(header, null));
        } catch (Throwable t) {
            throw new ExecutionException(t.getMessage(), t);
        }
    }

    @Override
    public void caught(Channel channel, Throwable e) throws RemoteException {
        try {
            this.executors.execute(new ChannelEventTask<Throwable>(this.handler, channel, ChannelEventTask.EventType.EXCEPTION, e));
        } catch (Throwable t) {
            throw new ExecutionException(t.getMessage(), t);
        }
    }
}
