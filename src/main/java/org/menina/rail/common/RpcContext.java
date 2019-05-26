package org.menina.rail.common;

import lombok.Builder;
import lombok.Data;
import org.menina.rail.config.BaseOptions;

/**
 * @author zhenghao
 * @date 2019/1/12
 */
@Builder
@Data
public class RpcContext {

    private BaseOptions options;

}
