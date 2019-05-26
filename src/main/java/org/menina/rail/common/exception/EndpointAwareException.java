package org.menina.rail.common.exception;

import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * @author zhenghao
 * @date 2019/1/17
 */
@Data
public class EndpointAwareException extends RemoteException {

    private Long requestId;

    public EndpointAwareException(Long requestId, String message) {
        super(message);
        Preconditions.checkNotNull(requestId);
        this.requestId = requestId;
    }

    public EndpointAwareException(Long requestId, String message, Throwable cause) {
        super(message, cause);
        Preconditions.checkNotNull(requestId);
        this.requestId = requestId;
    }
}
