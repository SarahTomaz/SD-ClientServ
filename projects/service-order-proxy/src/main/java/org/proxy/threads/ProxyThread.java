package org.proxy.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private String clientInput;
    private PrintWriter clientOutput;

    private String appServerIp;           // IP do servidor de aplicação
    private int appServerPort;            // Porta do servidor de aplicação
    private File logFile;

    public ProxyThread(Socket clientSocket, File file, String appServerIp, int appServerPort, String readLine, PrintWriter clientOutput) {
        this.clientSocket = clientSocket;
        this.logFile = file;
        this.appServerIp = appServerIp;
        this.appServerPort = appServerPort;
        
        this.clientInput = readLine;
        this.clientOutput = clientOutput;
    }

    @Override
    public void run() {
        try {
            String request = this.clientInput;
            String response = processarRequisicao(request);

            // pegar o json e enviar para o cache

            final JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

            final String cache_id = jsonObject.get("cache_id").getAsString();


            clientOutput.println(response);
            clientOutput.flush();
        } finally {
            // Fechar conexão
            try {
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
            // Conexão com o servidor de aplicação
            serverSocket = new Socket(appServerIp, appServerPort);

            /*
            * Troquei clientSocket por serverSocket
            * O clientSocket é a conexão com o cliente, e o serverSocket é a conexão com o servidor de aplicação.
            * A comunicação com o servidor de aplicação é feita através do serverSocket, que é a conexão estabelecida com o servidor de aplicação. */
            serverOutput = new PrintWriter(serverSocket.getOutputStream(), true);
            serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            // Envia requisição para o servidor de aplicação
            serverOutput.println(requisicao);
            serverOutput.flush();

            // Recebe resposta do servidor de aplicação
            resposta = serverInput.readLine();

            // Log da operação
            registrarLog("Servidor de aplicação: " + requisicao + " - Response: " + resposta);

        } catch (IOException e) {
            registrarLog("ERRO: " + e.getMessage());
            resposta = "ERRO: " + e.getMessage(); // Retorna erro para o cliente
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