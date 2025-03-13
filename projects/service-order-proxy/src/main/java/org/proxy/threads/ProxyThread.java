package org.proxy.threads;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader clientInput;
    private PrintWriter clientOutput;

    private String appServerIp;           // IP do servidor de aplicação
    private int appServerPort;            // Porta do servidor de aplicação
    private File logFile;

    public ProxyThread(Socket clientSocket, File file, String appServerIp, int appServerPort) {
        this.clientSocket = clientSocket;
        this.logFile = file;
        this.appServerIp = appServerIp;
        this.appServerPort = appServerPort;

        try {
            this.clientInput =
                    new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            this.clientOutput =
                    new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Erro ao inicializar streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = clientInput.readLine();

                String response = processarRequisicao(request);

                clientOutput.println(response);
                clientOutput.flush();
            }
        } catch (EOFException e) {
            System.out.println("Cliente desconectou-se.");
        } catch (IOException e) {
            System.out.println("Erro na comunicação: " + e.getMessage());
        } finally {
            // Fechar conexão
            try {
                if (clientInput != null) clientInput.close();
                if (clientOutput != null) clientOutput.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    private String processarRequisicao(String requisicao) {
        registrarLog("Requisição: " + requisicao + " - " + new Date());
        return comunicarComServidor(requisicao);
    }

    // Método para comunicar com o servidor de aplicação
    private String comunicarComServidor(String requisicao) {
        Socket serverSocket = null;
        PrintWriter serverOutput = null;
        BufferedReader serverInput = null;
        String resposta = "";

        try {
            serverSocket = new Socket(appServerIp, appServerPort);

            serverInput =
                    new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            serverOutput =
                    new PrintWriter(clientSocket.getOutputStream(), true);

            serverOutput.println(requisicao);
            serverOutput.flush();

            resposta = serverInput.readLine();

            // Log da operação
            registrarLog("Servidor de aplicação: " + requisicao + " - Response: " + resposta);

        } catch (IOException e) {
            registrarLog("ERRO: " + e.getMessage());
        } finally {
            // Fechar conexão
            try {
                if (serverInput != null) serverInput.close();
                if (serverOutput != null) serverOutput.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar conexão com servidor: " + e.getMessage());
            }
        }

        return resposta;
    }

    private synchronized void registrarLog(String mensagem) {
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println(new Date() + " - " + mensagem);

        } catch (IOException e) {
            System.out.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}