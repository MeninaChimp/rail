package org.menina.rail.handler;

import com.google.common.collect.Maps;
import org.menina.rail.common.RpcContext;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.common.exception.SerializeException;
import org.menina.rail.common.serialize.json.JsonSerializer;
import org.menina.rail.protocol.RpcHeader;
import org.menina.rail.protocol.RpcMessage;
import org.menina.rail.server.export.ServiceInfo;
import org.menina.rail.common.RpcConstants;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.common.exception.EndpointAwareException;
import org.menina.rail.common.proxy.Invocation;
import org.menina.rail.common.serialize.Serializer;
import org.menina.rail.common.serialize.SerializerAware;
import org.menina.rail.common.serialize.proto.ProtoSerializer;
import org.menina.rail.config.BaseOptions;
import org.menina.rail.server.export.ServiceRegister;
import org.menina.rail.transpot.Channel;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * @author zhenghao
 * @date 2019/1/14
 */
public class DecodeHandler extends ContextAwareChannelHandlerWrapper<RpcMessage<RpcHeader.RequestHeader>> implements SerializerAware {

    private Map<String, Serializer> serializers;

    public DecodeHandler(ChannelHandler handler, RpcContext context) {
        super(handler, context);
        Map<String, Serializer> inner = Maps.newHashMap();
        inner.put(BaseOptions.SerializerType.PROTOBUF.name(), new ProtoSerializer());
        inner.put(BaseOptions.SerializerType.JSON.name(), new JsonSerializer());
        this.serializers = Collections.unmodifiableMap(inner);
    }

    @Override
    public void receive(Channel channel, RpcMessage<RpcHeader.RequestHeader> message) throws RemoteException {
        RpcHeader.RequestHeader header = message.getHeader();
        String signature = RpcUtils.buildServiceKey(this.context.getOptions().getPort(), header.getAttachmentsOrDefault(RpcConstants.PATH_KEY, null), header.getMethodName());
        ServiceInfo serviceInfo = ServiceRegister.instance().find(signature);
        if (serviceInfo == null) {
            throw new EndpointAwareException(message.getHeader().getRequestId(),
                    "Failed to invoke method " + header.getMethodName() + " for service "
                            + header.getAttachmentsOrDefault(RpcConstants.PATH_KEY, null) + ", unregister error, make sure @Exporter had marked on the service provider");
        }

        Method method = serviceInfo.getMethod();
        Serializer serializer = this.getSerializer(header.getAttachmentsOrDefault(RpcConstants.SERIALIZER_KEY, null));
        if (serializer == null) {
            throw new EndpointAwareException(message.getHeader().getRequestId(), "Unknown Serializer Type :" + header.getAttachmentsOrDefault(RpcConstants.SERIALIZER_KEY, null));
        }

        Object payload;
        try {
            payload = serializer.decode(message.getBody(), serviceInfo.getRequestType());
        } catch (SerializeException e) {
            throw new EndpointAwareException(message.getHeader().getRequestId(), "Serialize failed :" + e.getMessage(), e);
        }

        Invocation invocation = Invocation.builder()
                .methodName(header.getMethodName())
                .requestId(header.getRequestId())
                .attachments(header.getAttachmentsMap())
                .responseType(method.getReturnType())
                .arguments(new Object[]{payload})
                .parametersType(method.getParameterTypes())
                .build();

        this.handler.receive(channel, invocation);
    }

    @Override
    public Serializer getSerializer(String type) {
        if (type == null) {
            return null;
        }

        return serializers.get(type);
    }

    @Override
    public RpcContext context() {
        return context;
    }
}
