package org.menina.rail.common.serialize.proto;

import com.google.common.base.Preconditions;
import com.google.protobuf.MessageLite;
import org.menina.rail.common.exception.SerializeException;
import org.menina.rail.common.serialize.Serializer;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
public class ProtoSerializer implements Serializer {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] bytes, Class<T> type) throws SerializeException {
        try {
            Preconditions.checkArgument(MessageLite.class.isAssignableFrom(type));
            return (T) type.getMethod("parseFrom", byte[].class).invoke(null, (Object) bytes);
        } catch (Exception e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte[] encode(Object object) throws SerializeException {
        try {
            Preconditions.checkArgument(object instanceof MessageLite);
            return ((MessageLite)object).toByteArray();
        } catch (Exception e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }
}
