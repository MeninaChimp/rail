package org.menina.rail.common.serialize;

import org.menina.rail.common.exception.SerializeException;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
public interface Serializer {

    <T> T decode(byte[] bytes, Class<T> type) throws SerializeException;

    byte[] encode(Object object) throws SerializeException;
}
