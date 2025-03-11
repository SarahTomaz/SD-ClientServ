
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServiceOrderClient {
    private static final String LOCATION_SERVER_HOST = "localhost"; // Altere para o endereço do servidor de localização
    private static final int LOCATION_SERVER_PORT = 8000; // Porta do servidor de localização
    
    private String applicationServerHost;
    private int applicationServerPort;
    private Scanner scanner;
    
    public ServiceOrderClient() {
        scanner = new Scanner(System.in);
        connectToLocationServer();
    }
    
    private void connectToLocationServer() {
        try (
            Socket socket = new Socket(LOCATION_SERVER_HOST, LOCATION_SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("GET_APPLICATION_SERVER");
            String response = in.readLine();
            String[] serverInfo = response.split(":");
            applicationServerHost = serverInfo[0];
            applicationServerPort = Integer.parseInt(serverInfo[1]);
            System.out.println("Servidor de aplicação encontrado em " + applicationServerHost + ":" + applicationServerPort);
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor de localização: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private String sendRequest(String request) {
        try (
            Socket socket = new Socket(applicationServerHost, applicationServerPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(request);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.equals("END_OF_RESPONSE")) {
                response.append(line).append("\n");
            }
            return response.toString();
        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o servidor: " + e.getMessage());
            return "ERRO: " + e.getMessage();
        }
    }
    
    public void searchServiceOrder() {
        System.out.print("Digite o código da OS para busca: ");
        String code = scanner.nextLine();
        String result = sendRequest("SEARCH:" + code);
        System.out.println("\nResultado da busca:");
        System.out.println(result);
    }
    
    public void listAllServiceOrders() {
        String result = sendRequest("LIST_ALL");
        System.out.println("\nListagem de todas as Ordens de Serviço:");
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
        
        // A hora será gerada pelo servidor
        String requestData = String.format("CREATE:%s,%s,%s", code, name, description);
        String result = sendRequest(requestData);
        
        System.out.println("\nResultado do cadastro:");
        System.out.println(result);
    }
    
    public void updateServiceOrder() {
        System.out.println("\nAlteração de Ordem de Serviço");
        
        System.out.print("Digite o código da OS a ser alterada: ");
        String code = scanner.nextLine();
        
        System.out.print("Novo nome (deixe em branco para manter): ");
        String name = scanner.nextLine();
        
        System.out.print("Nova descrição (deixe em branco para manter): ");
        String description = scanner.nextLine();
        
        String requestData = String.format("UPDATE:%s,%s,%s", code, name, description);
        String result = sendRequest(requestData);
        
        System.out.println("\nResultado da alteração:");
        System.out.println(result);
    }
    
    public void removeServiceOrder() {
        System.out.print("\nDigite o código da OS a ser removida: ");
        String code = scanner.nextLine();
        
        System.out.print("Tem certeza que deseja remover a OS " + code + "? (s/n): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("s")) {
            String result = sendRequest("REMOVE:" + code);
            System.out.println("\nResultado da remoção:");
            System.out.println(result);
        } else {
            System.out.println("Operação cancelada.");
        }
    }
    
    public void getRecordCount() {
        String result = sendRequest("COUNT");
        System.out.println("\nQuantidade de registros:");
        System.out.println(result);
    }
    
    public void executeSimulation() {
        System.out.println("Executando simulação conforme especificado:");
        
        System.out.println("\n=== REALIZANDO TRÊS CONSULTAS ===");
        for (int i = 0; i < 3; i++) {
            searchServiceOrder();
        }
        
        System.out.println("\n=== REALIZANDO LISTAGEM ===");
        listAllServiceOrders();
        
        System.out.println("\n=== REALIZANDO CADASTRO ===");
        createServiceOrder();
        
        System.out.println("\n=== REALIZANDO LISTAGEM ===");
        listAllServiceOrders();
        
        System.out.println("\n=== REALIZANDO OUTRO CADASTRO ===");
        createServiceOrder();
        
        System.out.println("\n=== REALIZANDO LISTAGEM ===");
        listAllServiceOrders();
        
        System.out.println("\n=== REALIZANDO ALTERAÇÃO ===");
        updateServiceOrder();
        
        System.out.println("\n=== REALIZANDO LISTAGEM ===");
        listAllServiceOrders();
        
        System.out.println("\n=== REALIZANDO REMOÇÃO ===");
        removeServiceOrder();
        
        System.out.println("\n=== REALIZANDO LISTAGEM ===");
        listAllServiceOrders();
        
        System.out.println("\n=== REALIZANDO OUTRA REMOÇÃO ===");
        removeServiceOrder();
        
        System.out.println("\n=== REALIZANDO LISTAGEM FINAL ===");
        listAllServiceOrders();
    }
    
    public void showMenu() {
        while (true) {
            System.out.println("\n===== SISTEMA DE ORDENS DE SERVIÇO =====");
            System.out.println("1. Buscar Ordem de Serviço");
            System.out.println("2. Listar todas as Ordens de Serviço");
            System.out.println("3. Cadastrar nova Ordem de Serviço");
            System.out.println("4. Alterar Ordem de Serviço");
            System.out.println("5. Remover Ordem de Serviço");
            System.out.println("6. Consultar quantidade de registros");
            System.out.println("7. Executar simulação automática");
            System.out.println("8. Sair");
            System.out.print("\nEscolha uma opção: ");
            
            String option = scanner.nextLine();
            
            switch (option) {
                case "1":
                    searchServiceOrder();
                    break;
                case "2":
                    listAllServiceOrders();
                    break;
                case "3":
                    createServiceOrder();
                    break;
                case "4":
                    updateServiceOrder();
                    break;
                case "5":
                    removeServiceOrder();
                    break;
                case "6":
                    getRecordCount();
                    break;
                case "7":
                    executeSimulation();
                    break;
                case "8":
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