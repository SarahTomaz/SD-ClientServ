package org.serviceorder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {
    private static ConnectionJDBC instance;
    private Connection connection;

    public ConnectionJDBC() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/service-soo";
        String username = "service-soo";
        String password = "service-soo";

        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database Connection Created With success!!");
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static ConnectionJDBC getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionJDBC();
        } else if (instance.getConnection().isClosed()) {
            instance = new ConnectionJDBC();
        }

        return instance;
    }
}
