package org.menina.rail.server.export;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenghao
 * @date 2019/1/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceRegister {

    private static final ServiceRegister INNER = new ServiceRegister();

    private ConcurrentHashMap<String, ServiceInfo> container = new ConcurrentHashMap<>();

    public static ServiceRegister instance(){
        return INNER;
    }

    public void export(String signature, ServiceInfo serviceInfo){
        container.put(signature, serviceInfo);
    }

    public void cancel(String signature){
        container.remove(signature);
    }

    public ServiceInfo find(String signature){
        return container.get(signature);
    }

}
