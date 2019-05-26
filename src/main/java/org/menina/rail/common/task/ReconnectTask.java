package org.menina.rail.common.task;

import org.menina.rail.client.Client;
import org.menina.rail.common.exception.RemoteException;
import org.menina.rail.transpot.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhenghao
 * @date 2019/1/16
 */
@Slf4j
@Data
public class ReconnectTask implements Runnable {

    private Client client;

    public ReconnectTask(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Channel channel = this.client.getChannel();
        if (!channel.isConnected()) {
            try {
                this.client.reconnect();
            } catch (RemoteException e) {
//                log.debug(e.getMessage(), e);
            }
        }
    }
}
