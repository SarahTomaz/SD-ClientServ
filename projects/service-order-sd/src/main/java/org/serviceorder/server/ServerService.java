package org.serviceorder.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;

public class ServerService {

    private static final String server = "localhost";
    private static final int port = 56789;

    public void initializeServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server initiated in port: " + port);

            RequestRedirect requestRedirect = new RequestRedirect();
            requestRedirect.setUpAllowedOperations();

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader income =
                       new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));

                PrintWriter outcome =
                        new PrintWriter(socket.getOutputStream(), true);

                String requisicao = income.readLine();
                System.out.println("Requisição recebida: " + requisicao);

                String response = requestRedirect.redirect(requisicao);
                outcome.println(response);

                outcome.close();
                income.close();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
