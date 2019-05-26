package org.menina.rail.handler;

import org.menina.rail.common.RpcContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.menina.rail.transpot.Channel;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
@Slf4j
public class IdleChannelHandler extends ContextAwareChannelHandlerWrapper {

    public IdleChannelHandler(ChannelHandler handler, RpcContext context) {
        super(handler, context);
    }

    @Override
    public void userEventTriggered(Channel channel, Object event) {
        if (event instanceof IdleStateEvent
                && IdleStateEvent.ALL_IDLE_STATE_EVENT.equals(event)) {
            log.warn("Detect channel {} inactive for {} ms, channel will be close.", channel.toString(), context().getOptions().getMaxIdleTimeMills() * 2);
            channel.close();
        }
    }

    @Override
    public RpcContext context() {
        return this.context;
    }
}
