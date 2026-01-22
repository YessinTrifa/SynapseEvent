package com.synapseevent.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnection {
    private static MaConnection instance;
    private Connection connection;

    private MaConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/synapse_event";
            String user = "root"; // Change as needed
            String password = ""; // Change as needed
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MaConnection getInstance() {
        if (instance == null) {
            instance = new MaConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
