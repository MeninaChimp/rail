package org.menina.rail.config;

import org.menina.rail.common.RpcConstants;
import lombok.Data;

/**
 * @author zhenghao
 * @date 2019/1/13
 *
 * wait for overwriting by @SuperBuilder
 */
@Data
public abstract class BaseOptions {

    private String basePackage = RpcConstants.BASE_PACKAGE;

    private int threadPoolSize = 200;

    private boolean tpsLimitEnable;

    private Double tpsLimit;

    private long invokeTimeoutMills = 3000;

    private int maxIdleTimeMills = 15 * 1000;

    private int retries = 0;

    private boolean shareConnection = true;

    private int connections = 1;

    private Enum serializer = SerializerType.PROTOBUF;

    public enum Side {
        /**
         * Mark consumer config
         */
        CLIENT,
        /**
         * Mark provider config
         */
        SERVER
    }

    public enum SerializerType{
        /**
         * Mark ProtoSerializer
         */
        PROTOBUF,

        /**
         * Mark JsonSerializer
         */
        JSON
    }

    public abstract Side currentSide();

    /**
     * So far, @SuperBuilder support for IDEA is unavailable
     * @return port
     */
    public abstract int getPort();
}
