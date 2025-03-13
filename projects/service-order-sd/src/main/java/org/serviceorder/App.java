package org.serviceorder;
import org.serviceorder.server.ServerService;

public class App 
{
    public static void main( String[] args )
    {
        ServerService serverService = new ServerService();
        serverService.initializeServer();
    }
}
