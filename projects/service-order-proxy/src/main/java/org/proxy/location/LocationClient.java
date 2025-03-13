package org.proxy.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocationClient {
    private final int proxyLocalizationServicePort = 65478;
    
    public void getApplicationServerAddress() throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;


        
        try {
            ServerSocket serverSocket = new ServerSocket(proxyLocalizationServicePort);
            socket = serverSocket.accept();

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println("GET_APPLICATION_SERVER");
            
            serverSocket.close();
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        }
    }
}