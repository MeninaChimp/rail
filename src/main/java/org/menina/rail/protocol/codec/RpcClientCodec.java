package org.menina.rail.protocol.codec;

import org.menina.rail.protocol.codec.proto.ProtoRequestEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;
import org.menina.rail.protocol.codec.proto.ProtoResponseDecoder;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public class RpcClientCodec extends CombinedChannelDuplexHandler<ProtoResponseDecoder, ProtoRequestEncoder> {

    public RpcClientCodec() {
        super(new ProtoResponseDecoder(), new ProtoRequestEncoder());
    }
}
