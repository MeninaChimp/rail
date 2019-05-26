package org.menina.rail.client;

/**
 * @author zhenghao
 * @date 2019/4/9
 */
public interface ConnectStateListener {

    void onConnected(String address, int port);

    void onDisconnected(String address, int port);
}
