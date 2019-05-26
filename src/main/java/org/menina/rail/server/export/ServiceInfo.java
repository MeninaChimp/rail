package org.menina.rail.server.export;

import org.menina.rail.common.proxy.Exporter;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 *
 * @author zhenghao
 * @date 2019/1/8
 */
@Builder
@Data
public class ServiceInfo {

    private Method method;

    private Exporter exporter;

    private Class<?> requestType;

    private Class<?> returnType;

}
