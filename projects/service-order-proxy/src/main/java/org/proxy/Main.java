package org.proxy;
import org.proxy.server.Server;

public class Main {
    public static void main(String[] args) {
        int proxyPort = 54321;
        String appServerIp = "localhost";
        int appServerPort = 56789;

        new Server(proxyPort, appServerIp, appServerPort);
    }
}