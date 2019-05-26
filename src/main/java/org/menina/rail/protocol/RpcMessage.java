package org.menina.rail.protocol;

import com.google.protobuf.MessageLite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhenghao
 * @date 2018/12/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcMessage<T extends MessageLite> {

    private T header;

    private byte[] body;
}
