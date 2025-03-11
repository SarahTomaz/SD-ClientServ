import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Cliente para o Servidor de Localização
 * Solicita o endereço do servidor de aplicação
 */
public class LocationClient {
    // Endereço e porta do servidor de localização
    private static final String LOCATION_SERVER_ADDRESS = "26.60.139.41";       //  Ip do 
                                                                            //Se for rodar no mesmo
                                                                           // computador pode utilizar "Localhost"
    private static final int LOCATION_SERVER_PORT = 8000;

    /**
     * Obtém o endereço e porta do servidor de aplicação
     * 
     * @return Objeto ServerInfo contendo endereço e porta, ou null em caso de erro
     */
    public static ServerInfo getApplicationServerInfo() {
        try (
                Socket socket = new Socket(LOCATION_SERVER_ADDRESS, LOCATION_SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            // Envia uma solicitação para o servidor de localização
            out.writeObject("REQUEST_APP_SERVER_INFO");

            // Recebe a resposta
            ServerInfo serverInfo = (ServerInfo) in.readObject();
            System.out.println("Recebido endereço do servidor de aplicação: " + serverInfo);

            return serverInfo;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao obter informações do servidor de aplicação: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        // Exemplo de uso
        ServerInfo appServerInfo = getApplicationServerInfo();
        if (appServerInfo != null) {
            System.out.println("Conectando ao servidor de aplicação em " +
                    appServerInfo.getAddress() + ":" + appServerInfo.getPort());

            // Aqui você poderia continuar com a lógica para se conectar ao servidor de
            // aplicação
        } else {
            System.out.println("Não foi possível obter informações do servidor de aplicação.");
        }
    }
}