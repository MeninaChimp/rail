package org.menina.rail.common;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
public class RpcConstants {

    public static final String BASE_PACKAGE = "org.menina";

    public static final int HEADER_LENGTH_OFFSET = 4;

    public static final String PATH_KEY = "path";

    public static final String VERSION_KET = "version";

    public static final String CURRENT_VERSION = "1.0.0";

    public static final String RETRY_KET = "retries";

    public static final String REQUEST_KEY = "requestId";

    public static final String SERIALIZER_KEY = "serializer";

    public static final byte SCOPE_CONSUMER_ENABLE = (byte) (Scope.CONSUMER.getValue() | Scope.ALL.getValue());

    public static final byte SCOPE_PROVIDER_ENABLE = (byte) (Scope.PROVIDER.getValue() | Scope.ALL.getValue());

}
