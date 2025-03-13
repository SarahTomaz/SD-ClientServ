package org.example.serializers;

public class ServerInfo implements java.io.Serializable {
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
