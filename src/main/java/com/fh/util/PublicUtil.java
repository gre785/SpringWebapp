
package com.fh.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PublicUtil
{

    public static void main(String[] args)
    {
        System.out.println("ip=" + PublicUtil.getIp());
    }

    public static String getPorjectPath()
    {
        return System.getProperty("user.dir") + "/";
    }

    public static String getIp()
    {
        String ip = "";
        try {
            InetAddress inet = InetAddress.getLocalHost();
            ip = inet.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
