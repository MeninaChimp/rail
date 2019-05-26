package org.menina.rail.protocol.codec.proto;

import org.menina.rail.common.RpcConstants;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public class ProtoRequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RpcConstants.HEADER_LENGTH_OFFSET) {
            return;
        }

        in.markReaderIndex();
        int headerLength = in.readInt();
        if (in.readableBytes() < headerLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[headerLength];
        in.readBytes(data);
        RpcHeader.RequestHeader header = RpcHeader.RequestHeader.parseFrom(data);
        int bodyLength = header.getBodyLength();
        if (bodyLength == 0) {
            RpcMessage<RpcHeader.RequestHeader> rpcMessage = new RpcMessage<>(header, null);
            out.add(rpcMessage);
            return;
        }

        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }


        byte[] body = new byte[bodyLength];
        in.readBytes(body);
        RpcMessage<RpcHeader.RequestHeader> rpcMessage = new RpcMessage<>(header, body);
        out.add(rpcMessage);
    }
}
