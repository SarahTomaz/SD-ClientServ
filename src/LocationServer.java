import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Servidor de Localização para o sistema de Ordens de Serviço
 * Responsável por informar aos clientes o endereço do servidor de aplicação
 */
public class LocationServer {
    // Porta onde o servidor de localização vai escutar
    private static final int PORT = 56789;

    // Endereço e porta do servidor de aplicação
    private static final String APP_SERVER_ADDRESS = "10.215.32.41";   // Ip do servidor de aplicação (vitor) -
                                                                        // Se for rodar no mesmo computador pode utilizar
                                                                        // "Localhost"
    private static final int APP_SERVER_PORT = 56790;

    // Logger para registro de operações
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

                    // Cria uma thread para atender esse cliente
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

    /**
     * Configura o logger para salvar os registros em um arquivo
     */
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

    /**
     * Classe interna para lidar com requisições de clientes
     */
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                // Recebe a mensagem do cliente (pode ser apenas uma solicitação de endereço)
                String request = (String) in.readObject();
                logger.info("Requisição recebida: " + request);

                // Prepara a resposta com o endereço e porta do servidor de aplicação
                ServerInfo serverInfo = new ServerInfo(APP_SERVER_ADDRESS, APP_SERVER_PORT);

                // Envia a resposta para o cliente
                out.writeObject(serverInfo);
                logger.info("Endereço do servidor de aplicação enviado para: " +
                        clientSocket.getInetAddress().getHostAddress());

                // Fecha a conexão
                clientSocket.close();

            } catch (IOException | ClassNotFoundException e) {
                logger.severe("Erro ao processar requisição do cliente: " + e.getMessage());
                System.err.println("Erro ao processar requisição do cliente: " + e.getMessage());
            }
        }
    }
}

/**
 * Classe que representa as informações do servidor de aplicação
 * Esta classe será serializada e enviada para o cliente
 */
class ServerInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String address;
    private int port;

    public ServerInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Servidor de Aplicação em " + address + ":" + port;
    }
}