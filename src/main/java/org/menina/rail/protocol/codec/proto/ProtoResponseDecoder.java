package org.menina.rail.protocol.codec.proto;

import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.menina.rail.common.RpcConstants;

import java.util.List;

/**
 * @author zhenghao
 * @date 2019/1/10
 */
public class ProtoResponseDecoder extends ByteToMessageDecoder {

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

        byte[] headerBytes = new byte[headerLength];
        in.readBytes(headerBytes);
        RpcHeader.ResponseHeader header = RpcHeader.ResponseHeader.parseFrom(headerBytes);
        if (header.getBodyLength() == 0) {
            RpcMessage<RpcHeader.ResponseHeader> response = new RpcMessage<>(header, null);
            out.add(response);
            return;
        }

        if (in.readableBytes() < header.getBodyLength()) {
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[header.getBodyLength()];
        in.readBytes(body);
        RpcMessage<RpcHeader.ResponseHeader> response = new RpcMessage<>(header, body);
        out.add(response);
    }
}
