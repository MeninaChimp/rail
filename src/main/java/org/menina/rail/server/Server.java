package org.menina.rail.server;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2018/12/13
 */
public interface Server {

    void start();

    void close();

    boolean running();

    CompletableFuture closeFuture();

}
