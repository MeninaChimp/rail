package org.menina.rail.test.api;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/8
 */
public interface MockService {

    CompletableFuture<SetMessage.Response> mock(SetMessage.Request request);
}
