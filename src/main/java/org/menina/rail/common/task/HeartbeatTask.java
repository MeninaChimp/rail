package org.menina.rail.common.task;

import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.client.Client;
import org.menina.rail.common.RpcUtils;
import org.menina.rail.transpot.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhenghao
 * @date 2019/1/15
 */
@Data
@Slf4j
public class HeartbeatTask implements Runnable {

    private Client client;

    public HeartbeatTask(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Channel channel = this.client.getChannel();
        if (!channel.isConnected()) {
            return;
        }

        try {
            channel.send(RpcUtils.newHeartbeat());
        } catch (RemoteException e) {
            log.warn("Heartbeat failed, error message {}", e.getMessage());
        }
    }
}
