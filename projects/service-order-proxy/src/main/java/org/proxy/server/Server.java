package org.proxy.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.proxy.threads.ProxyThread;

public class Server {
    private int proxyPort;    // Socket para receber conexões dos clientes
    private String appServerIp;           // IP do servidor de aplicação
    private int appServerPort;            // Porta do servidor de aplicação
    static private File logFile;

    public Server(int proxyPort, String appServerIp, int appServerPort) {
        this.proxyPort = proxyPort;
        this.appServerIp = appServerIp;
        this.appServerPort = appServerPort;
        this.logFile = new File("proxy_log.txt");
        initializeProxyServer();
    }

    public void initializeProxyServer() {
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            ServerSocket serverSocket = new ServerSocket(proxyPort);
            System.out.println("Proxy iniciado na porta " + proxyPort);
            System.out.println("Conectado ao servidor de aplicação em " + appServerIp + ":" + appServerPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão de cliente: " + clientSocket.getInetAddress().getHostAddress());

                BufferedReader clientInput =
                    new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter clientOutput =
                        new PrintWriter(clientSocket.getOutputStream(), true);

                String readLine = clientInput.readLine();

                if (readLine.equals("GET_APPLICATION_SERVER")) {
                    clientOutput.println(clientSocket.getInetAddress() + ":" + appServerPort);

                    System.out.println("Endereço do servidor de aplicação obtido: " + appServerIp + ":" + appServerPort);

                    clientOutput.close();
                    clientInput.close();
                    clientSocket.close();
                    continue;
                }
                
                // Criar uma thread para tratar este cliente
                ProxyThread proxyThread = new ProxyThread(clientSocket, logFile, appServerIp, appServerPort, readLine, clientOutput);
                Thread thread = new Thread(proxyThread);
                thread.start();

                clientInput.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
