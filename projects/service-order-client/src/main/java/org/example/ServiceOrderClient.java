package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.JsonObject;

public class ServiceOrderClient {
    private static final String LOCATION_SERVER_HOST = "localhost";
    private static final int LOCATION_SERVER_PORT = 8000;

    private String applicationServerProxyHost;
    private int applicationServerProxyPort;
    private Scanner scanner;

    public ServiceOrderClient() {
        scanner = new Scanner(System.in);
        this.connectToLocationServer();
    }

    private void connectToLocationServer() {
       try (Socket socket = new Socket(LOCATION_SERVER_HOST, LOCATION_SERVER_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("GET_APPLICATION_SERVER");
            String response = in.readLine();
            String[] serverInfo = response.split(":");
            this.applicationServerProxyHost = serverInfo[0];
            this.applicationServerProxyPort = Integer.parseInt(serverInfo[1]);
            System.out.println("Servidor de aplicação encontrado em " + applicationServerProxyHost + ":" + applicationServerProxyPort);
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor de localização: " + e.getMessage());
            System.exit(1);
        }
    }

    private String sendRequest(String request) {
        try (
                Socket socket = new Socket(applicationServerProxyHost, applicationServerProxyPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(request);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.equals("END_OF_RESPONSE")) {
                response.append(line).append("\n");
            }

            out.close();
            in.close();
            socket.close();
            return response.toString();
        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o servidor: " + e.getMessage());
            return "ERRO: " + e.getMessage();
        }
    }

    public void listAllServiceOrders() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "list");
        String operation = jsonObject.toString();
        System.out.println(operation);
        String result = sendRequest(operation);
        System.out.println("\nList all service orders:");
        System.out.println(result);
    }

    public void createServiceOrder() {
        System.out.println("\nCadastro de Nova Ordem de Serviço");

        System.out.print("Código: ");
        String code = scanner.nextLine();

        System.out.print("Nome: ");
        String name = scanner.nextLine();

        System.out.print("Descrição: ");
        String description = scanner.nextLine();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "add");

        jsonObject.addProperty("code", code);
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("name", name);

        String operation = jsonObject.toString();
        String result = sendRequest(operation);

        System.out.println("\nResultado do cadastro:");
        System.out.println(result);
    }

    public void updateServiceOrder() {
        System.out.println("\nAlteração de Ordem de Serviço");

        System.out.print("Digite o Id da ordem a ser atualizada: ");
        String id = scanner.nextLine();

        System.out.print("Digite o código da OS a ser alterada: ");
        String code = scanner.nextLine();

        System.out.print("Digite o novo nome da ordem de serviço: ");
        String name = scanner.nextLine();

        System.out.print("Digite a nova descrição da ordem de serviço: ");
        String description = scanner.nextLine();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "update");

        jsonObject.addProperty("code", code);
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("id", id);

        String operation = jsonObject.toString();

        String result = sendRequest(operation);

        System.out.println("\nResultado da alteração:");
        System.out.println(result);
    }

    public void removeServiceOrder() {
        System.out.print("\nDigite o Id da OS a ser removida: ");
        String code = scanner.nextLine();

        System.out.print("Tem certeza que deseja remover a OS " + code + "? (s/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("s")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("operation", "delete");
            jsonObject.addProperty("id", Integer.parseInt(code));

            String operation = jsonObject.toString();

            String result = sendRequest(operation);
            System.out.println("\nResultado da remoção:");
            System.out.println(result);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    public void getRecordCount() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "list_quantity");
        String operation = jsonObject.toString();

        String result = sendRequest(operation);
        System.out.println("\nQuantidade de registros:");
        System.out.println(result);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n===== SISTEMA DE ORDENS DE SERVIÇO =====");
            System.out.println("1. Listar todas as Ordens de Serviço");
            System.out.println("2. Cadastrar nova Ordem de Serviço");
            System.out.println("3. Alterar Ordem de Serviço");
            System.out.println("4. Remover Ordem de Serviço");
            System.out.println("5. Consultar quantidade de registros");
            System.out.println("6. Sair");
            System.out.print("\nEscolha uma opção: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    listAllServiceOrders();
                    break;
                case "2":
                    createServiceOrder();
                    break;
                case "3":
                    updateServiceOrder();
                    break;
                case "4":
                    removeServiceOrder();
                    break;
                case "5":
                    getRecordCount();
                    break;
                case "6":
                    System.out.println("Encerrando o programa. Até mais!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando cliente de Ordens de Serviço...");
        ServiceOrderClient client = new ServiceOrderClient();
        client.showMenu();
    }
}