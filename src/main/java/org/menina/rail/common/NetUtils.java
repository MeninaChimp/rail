package org.menina.rail.common;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by zhenghao on 2019/1/8.
 */
@Slf4j
public class NetUtils {

    private static final String LOCALHOST = "127.0.0.1";
    private static final String ANYHOST = "0.0.0.0";
    private static volatile InetAddress innerLocalAddress = null;
    private static Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public NetUtils() {
    }

    public static InetAddress getLocalAddress() {
        if(innerLocalAddress == null) {
            innerLocalAddress = getLocalAddress0();
        }

        return innerLocalAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;

        try {
            localAddress = InetAddress.getLocalHost();
            if(isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable t) {
            log.warn("Failed to retriving ip address, " + t.getMessage(), t);
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if(interfaces != null) {
                while(interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = (NetworkInterface)interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if(addresses != null) {
                            while(addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = (InetAddress)addresses.nextElement();
                                    if(isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable t) {
                                    log.warn("Failed to retriving ip address, " + t.getMessage(), t);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        log.warn("Failed to retriving ip address, " + t.getMessage(), t);
                    }
                }
            }
        } catch (Throwable t) {
            log.warn("Failed to retriving ip address, " + t.getMessage(), t);
        }

        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if(address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }

    public static void setIpPattern(Pattern ipPattern) {
        IP_PATTERN = ipPattern;
    }
}

