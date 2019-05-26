package org.menina.rail.protocol.codec.proto;

import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public class ProtoResponseEncoder extends MessageToByteEncoder<RpcMessage<RpcHeader.ResponseHeader>>{

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage<RpcHeader.ResponseHeader> msg, ByteBuf out) throws Exception {
        byte[] headerBytes = msg.getHeader().toByteArray();
        out.writeInt(headerBytes.length);
        ByteBuf buf = Unpooled.wrappedBuffer(headerBytes, msg.getBody());
        out.writeBytes(buf);
    }
}
