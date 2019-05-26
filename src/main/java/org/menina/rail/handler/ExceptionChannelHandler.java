package org.menina.rail.handler;

import org.menina.rail.common.exception.EndpointAwareException;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.transpot.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
@Slf4j
public class ExceptionChannelHandler extends AbstractChannelHandlerWrapper {

    public ExceptionChannelHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void caught(Channel channel, Throwable t) throws RemoteException {
        if (t instanceof EndpointAwareException) {
            log.error(t.getMessage());
            RpcHeader.ResponseHeader header = RpcHeader.ResponseHeader.newBuilder()
                    .setRequestId(((EndpointAwareException) t).getRequestId())
                    .setResponseCode(RpcHeader.ResponseCode.FAIL)
                    .setResponseMessage(t.getMessage())
                    .build();

            channel.send(new RpcMessage<RpcHeader.ResponseHeader>(header, null));
            return;
        }

        log.error("Channel {} unchecked exception caught, error message: {}", channel, t.getMessage(), t);
    }
}
