package com.portabull.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemUtils {

    public static void main(String args[]){
        InetAddress ip;
        String hostname;
        try {
            InetAddress myIP=InetAddress.getLocalHost();

            /* public String getHostAddress(): Returns the IP
             * address string in textual presentation.
             */
            System.out.println("My IP Address is:");
            System.out.println(myIP.getHostAddress());

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }
    }
}
