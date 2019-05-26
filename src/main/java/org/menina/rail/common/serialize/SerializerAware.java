package org.menina.rail.common.serialize;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
public interface SerializerAware {

    Serializer getSerializer(String type);
}
