package org.proxy;

import java.io.IOException;

import org.proxy.location.LocationClient;
import org.proxy.server.Server;

public class Main {
    public static void main(String[] args) {
        int proxyPort = 54321;
        String locationServerIp = "localhost"; // Endereço do servidor de localização
        int locationServerPort = 8000;         // Porta do servidor de localização
        
        try {
            // Obtém o endereço do servidor de aplicação do servidor de localização com locationcliente é comunicação entre servidores 
            LocationClient locationClient = new LocationClient(locationServerIp, locationServerPort);
            String[] appServerInfo = locationClient.getApplicationServerAddress();
            
            String appServerIp = appServerInfo[0];
            int appServerPort = Integer.parseInt(appServerInfo[1]);
            
            System.out.println("Endereço do servidor de aplicação obtido: " + appServerIp + ":" + appServerPort);
            
            // Inicia o servidor proxy com as informações obtidas
            new Server(proxyPort, appServerIp, appServerPort);
        } catch (IOException e) {
            System.err.println("Erro ao obter endereço do servidor de aplicação: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Formato inválido para porta do servidor de aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}