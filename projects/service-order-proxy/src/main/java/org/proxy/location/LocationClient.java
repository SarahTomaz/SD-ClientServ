package org.proxy.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LocationClient {
    private final String locationServerIP;
    private final int locationServerPort;
    
    public LocationClient(String locationServerIP, int locationServerPort) {
        this.locationServerIP = locationServerIP;
        this.locationServerPort = locationServerPort;
    }
    
    public String[] getApplicationServerAddress() throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        try {
            socket = new Socket(locationServerIP, locationServerPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println("GET_APPLICATION_SERVER");
            String response = in.readLine();
            
            return response.split(":");
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        }
    }
}