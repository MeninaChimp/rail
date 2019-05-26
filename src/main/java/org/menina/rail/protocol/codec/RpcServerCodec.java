package org.menina.rail.protocol.codec;

import org.menina.rail.protocol.codec.proto.ProtoRequestDecoder;
import org.menina.rail.protocol.codec.proto.ProtoResponseEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public class RpcServerCodec extends CombinedChannelDuplexHandler<ProtoRequestDecoder, ProtoResponseEncoder> {

    public RpcServerCodec() {
        super(new ProtoRequestDecoder(), new ProtoResponseEncoder());
    }
}
