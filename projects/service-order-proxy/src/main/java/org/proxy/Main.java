package org.proxy;

import org.proxy.server.Server;

public class Main {
    public static void main(String[] args) {
        int proxyPort = 54321;
        String AplicationServerIp = "localhost"; // Endereço do servidor de aplicação
        int AplicationServerPort = 54322;         // Porta do servidor de aplicação
        
        try {
            // Inicia o servidor proxy com as informações obtidas
            new Server(proxyPort, AplicationServerIp, AplicationServerPort);

        } catch (NumberFormatException e) {
            System.err.println("Formato inválido para porta do servidor de aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}