package org.menina.rail.common.serialize.json;

import org.menina.rail.common.exception.SerializeException;
import org.menina.rail.common.serialize.Serializer;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
public class JsonSerializer implements Serializer {
    @Override
    public <T> T decode(byte[] bytes, Class<T> type) throws SerializeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] encode(Object object) throws SerializeException {
        throw new UnsupportedOperationException();
    }
}
