package org.menina.rail.handler;

import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
@Slf4j
public class HeartbeatHandler extends AbstractChannelHandlerWrapper<RpcMessage<RpcHeader.RequestHeader>> {
    public HeartbeatHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void receive(Channel channel, RpcMessage<RpcHeader.RequestHeader> message) throws RemoteException {
        if (!RpcHeader.MessageType.HEARTBEAT.equals(message.getHeader().getMessageType())) {
            this.handler.receive(channel, message);
        } else {
            log.debug("Receive heartbeat from {}", channel.getRemoteAddress());
        }
    }
}
