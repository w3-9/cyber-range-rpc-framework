package org.young.irpc.framework.core.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @ClassName NetworkUtil
 * @Description TODO
 * @Author young
 * @Date 2023/2/18 下午9:00
 * @Version 1.0
 **/
@Slf4j
public class NetworkUtil {

    private static final String LOOPBACK = "127.0.0.1";

    public static String getIpAddress(){
        /**
         * 为测试方便，直接返回回环地址
         */
//
//        try {
//            Enumeration<NetworkInterface> interfaces
//                    =  NetworkInterface.getNetworkInterfaces();
//            while (interfaces.hasMoreElements()){
//                NetworkInterface interface_ =
//                    interfaces.nextElement();
//                    if (interface_.isLoopback() || interface_.isVirtual()){
//                        continue;
//                    }
//                    Enumeration<InetAddress> addresses
//                            = interface_.getInetAddresses();
//                    while (addresses.hasMoreElements()){
//                        InetAddress address = addresses.nextElement();
//                        if (address!=null && address instanceof Inet4Address){
//                            return address.getHostAddress();
//                        }
//                    }
//            }
//        } catch (SocketException e) {
//            log.error("cant find available inetAddress...");
//            e.printStackTrace();
//        }
        return LOOPBACK;
    }

}
