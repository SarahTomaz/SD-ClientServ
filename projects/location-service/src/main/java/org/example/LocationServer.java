package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.example.serializers.ServerInfo;

public class LocationServer {
    private static final int PORT = 8000;
    private static final String APP_SERVER_ADDRESS = "localhost";
    private static final int APP_SERVER_PORT = 54321;

    private static final Logger logger = Logger.getLogger("LocationServerLog");

    public static void main(String[] args) {
        setupLogger();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Localização iniciado na porta " + PORT);
            logger.info("Servidor de Localização iniciado na porta " + PORT);

            // Loop infinito para aceitar conexões
            while (true) {
                try {
                    // Aceita uma conexão de cliente
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nova conexão de cliente: " + clientSocket.getInetAddress().getHostAddress());
                    logger.info("Nova conexão de cliente: " + clientSocket.getInetAddress().getHostAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    logger.severe("Erro ao aceitar conexão: " + e.getMessage());
                    System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Erro ao iniciar o servidor: " + e.getMessage());
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("location_server_log.txt", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Desativa o log no console
        } catch (IOException e) {
            System.err.println("Erro ao configurar logger: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String request = in.readLine();
                logger.info("Requisição recebida: " + request);

                ServerInfo serverInfo = new ServerInfo(APP_SERVER_ADDRESS, APP_SERVER_PORT);

                // Envia a resposta para o cliente
                out.println(APP_SERVER_ADDRESS + ":" + APP_SERVER_PORT);
                logger.info("Endereço do servidor de aplicação enviado para: " +
                        clientSocket.getInetAddress().getHostAddress());

                // Fecha a conexão
                out.close();
                in.close();
                clientSocket.close();

            } catch (IOException e) {
                logger.severe("Erro ao processar requisição do cliente: " + e.getMessage());
                System.err.println("Erro ao processar requisição do cliente: " + e.getMessage());
            }
        }
    }
}
