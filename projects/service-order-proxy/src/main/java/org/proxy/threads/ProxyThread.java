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
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private String clientInput;
    private PrintWriter clientOutput;

    private String appServerIp;           // IP do servidor de aplicação
    private int appServerPort;            // Porta do servidor de aplicação
    private File logFile;
    
    // Cache FIFO
    private static final int CACHE_SIZE = 30;
    private static final Map<String, String> cache = new LinkedHashMap<String, String>(CACHE_SIZE + 1, 0.75f, false) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > CACHE_SIZE; 
        }
    };

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
            String response;
            
            // Chama o método generateCacheKey
            JsonObject requestJson = JsonParser.parseString(request).getAsJsonObject();
            String operation = requestJson.get("operation").getAsString();
            
            // Gerar uma chave de cache baseada na operação e seus parâmetros
            String cacheKey = generateCacheKey(requestJson);
            
            // Verificar se é uma operação de leitura
            boolean isReadOperation = "list".equals(operation) || "list_quantity".equals(operation);
            
            if (isReadOperation) {
                // Verificar se a resposta está na cache
                synchronized (cache) {
                    if (cache.containsKey(cacheKey)) {
                        registrarLog("CACHE HIT para operação: " + operation + " [Chave: " + cacheKey + "]");
                        response = cache.get(cacheKey);
                    } else {
                        registrarLog("CACHE MISS para operação: " + operation + " [Chave: " + cacheKey + "]");
                        // Se não estiver na cache, comunicar com o servidor
                        response = processarRequisicao(request);
                        
                        // Armazenar o resultado na cache
                        cache.put(cacheKey, response);
                    }
                }
            } else {
                // Operações de escrita invalidam a cache para "list" e "list_quantity"
                if ("add".equals(operation) || "update".equals(operation) || "delete".equals(operation)) {
                    invalidateCache();
                    registrarLog("Cache invalidada após operação de escrita: " + operation);
                }
                
                // Processar a requisição normalmente
                response = processarRequisicao(request);
            }

            printCacheStatus();
            
            // Enviar resposta para o cliente
            clientOutput.println(response);
            clientOutput.flush();
            
        } catch (Exception e) {
            registrarLog("Erro no processamento da requisição: " + e.getMessage());
            e.printStackTrace();
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

     // Gera uma chave única para cache baseada na operação e seus parâmetros
    private String generateCacheKey(JsonObject requestJson) {
        String operation = requestJson.get("operation").getAsString();
        StringBuilder key = new StringBuilder(operation);
        
        // Adicionar parâmetros específicos na chave dependendo da operação
        if (requestJson.has("id")) {
            key.append("_id_").append(requestJson.get("id").getAsString());
        }
        if (requestJson.has("code") && ("list".equals(operation) || "list_quantity".equals(operation))) {
            key.append("_code_").append(requestJson.get("code").getAsString());
        }
        
        return key.toString();
    }
    
    // Invalida entradas da cache relacionadas a operações de listagem
    private synchronized void invalidateCache() {
        synchronized (cache) {
            // Remover todas as entradas que começam com "list" ou "list_quantity"
            cache.entrySet().removeIf(entry -> 
                entry.getKey().startsWith("list") || entry.getKey().startsWith("list_quantity"));
        }
    }
    

    // Exibe o estado atual da cache no log
    private void printCacheStatus() {
        StringBuilder sb = new StringBuilder("Estado atual da cache:\n");
        
        synchronized (cache) {
            sb.append("Tamanho: ").append(cache.size()).append("/").append(CACHE_SIZE).append("\n");
            int count = 0;
            for (Map.Entry<String, String> entry : cache.entrySet()) {
                sb.append(count++).append(": ").append(entry.getKey()).append("\n");
            }
        }
        
        registrarLog(sb.toString());
    }

    private String processarRequisicao(String requisicao) {
        registrarLog("Requisição: " + requisicao + " - " + new Date());
        return comunicarComServidor(requisicao);
    }

    private String comunicarComServidor(String requisicao) {
        Socket serverSocket = null;
        PrintWriter serverOutput = null;
        BufferedReader serverInput = null;
        String resposta = "";

        try {
            // Conexão com o servidor de aplicação
            serverSocket = new Socket(appServerIp, appServerPort);

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
            System.out.println(new Date() + " - " + mensagem); // Também exibe no console para depuração

        } catch (IOException e) {
            System.out.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}