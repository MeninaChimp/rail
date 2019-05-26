package org.menina.rail.test.client;

import org.menina.rail.client.Reference;
import org.menina.rail.test.api.MockService;
import org.menina.rail.test.api.SetMessage;
import org.menina.rail.config.ClientOptions;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhenghao
 * @date 2019/1/13
 */
public class Client {

    public static void main(String[] args) {
        ClientOptions options = ClientOptions.builder()
                .remoteAddress("127.0.0.1")
                .port(20990)
                .build();
        Reference reference = new Reference(options);
        MockService mockService = reference.refer(MockService.class);
        CompletableFuture<SetMessage.Response> response = mockService.mock(SetMessage.Request.newBuilder()
                .setId(1)
                .setMessage("request")
                .build());
        response.whenComplete((v, t) -> {
            if (t != null) {
                System.out.println(t.getMessage());
            } else {
                System.out.println(v);
            }
        });

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        reference = new Reference(options);
        mockService = reference.refer(MockService.class);
        CompletableFuture<SetMessage.Response> response2 = mockService.mock(SetMessage.Request.newBuilder()
                .setId(2)
                .setMessage("request")
                .build());
        response2.whenComplete((v, t) -> {
            if (t != null) {
                System.out.println(t.getMessage());
            } else {
                System.out.println(v);
            }
        });

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
