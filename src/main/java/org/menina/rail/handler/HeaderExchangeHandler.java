package org.menina.rail.handler;

import com.google.protobuf.MessageLite;
import org.menina.rail.common.RpcConstants;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.common.exception.EndpointAwareException;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.proxy.Exporter;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.server.export.ServiceInfo;
import org.menina.rail.transpot.Channel;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.server.export.ServiceRegister;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
@Slf4j
public class HeaderExchangeHandler extends ContextAwareChannelHandlerWrapper<Invocation> {

    public HeaderExchangeHandler(ChannelHandler handler, RpcContext context) {
        super(handler, context);
    }

    @Override
    public void channelActive(Channel channel) throws RemoteException {
        log.info("Connected from {} --> {}", channel.getRemoteAddress(), channel.getLocalAddress());
    }

    @Override
    public void channelInactive(Channel channel) throws RemoteException {
        log.info("Disconnected from {} --> {}", channel.getRemoteAddress(), channel.getLocalAddress());
    }

    @Override
    public void receive(Channel channel, Invocation invocation) throws RemoteException {
        RpcMessage<RpcHeader.ResponseHeader> response = new RpcMessage<>();
        RpcHeader.ResponseHeader.Builder responseHeaderBuilder = RpcHeader.ResponseHeader.newBuilder()
                .setRequestId(invocation.getRequestId())
                .setResponseCode(RpcHeader.ResponseCode.SUCCESS)
                .setResponseMessage("");
        String signature = RpcUtils.buildServiceKey(this.context.getOptions().getPort(), invocation.getAttachments().get(RpcConstants.PATH_KEY), invocation.getMethodName());
        ServiceInfo serviceInfo = ServiceRegister.instance().find(signature);
        Exporter exporter = serviceInfo.getExporter();
        try {
            CompletableFuture<MessageLite> future = exporter.getInvoker().invoke(invocation);
            if (future.isDone()) {
                MessageLite result = future.get();
                byte[] body = result == null ? null : result.toByteArray();
                response.setHeader(responseHeaderBuilder.setBodyLength(body == null ? 0 : body.length).build());
                response.setBody(body);
                channel.send(response);
            } else {
                future.whenComplete((v, t) -> {
                    if (t != null) {
                        responseHeaderBuilder
                                .setResponseCode(RpcHeader.ResponseCode.FAIL)
                                .setResponseMessage(t.getMessage());
                        log.error(t.getMessage(), t);
                    } else {
                        try {
                            byte[] body = v == null ? null : v.toByteArray();
                            response.setHeader(responseHeaderBuilder.setBodyLength(body == null ? 0 : body.length).build());
                            response.setBody(body);
                            channel.send(response);
                        } catch (RemoteException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            throw new EndpointAwareException(invocation.getRequestId(), t.getMessage(), t);
        }
    }

    @Override
    public RpcContext context() {
        return this.context;
    }
}
