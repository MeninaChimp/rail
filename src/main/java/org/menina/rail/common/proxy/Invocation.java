package org.menina.rail.common.proxy;

import com.google.common.collect.Maps;
import org.menina.rail.common.RpcContext;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author zhenghao
 * @date 2019/1/11
 */
@Data
@Builder
public class Invocation {

    private String methodName;

    private long requestId;

    private Class<?> responseType;

    private Object[] arguments;

    private Class<?>[] parametersType;

    private RpcContext context;

    private int failed;

    @Builder.Default
    private Map<String, String> attachments = Maps.newHashMap();

    public Invocation addAttachment(String key, String value) {
        attachments.put(key, value);
        return Invocation.this;
    }

    public void fail() {
        this.failed++;
    }

}
