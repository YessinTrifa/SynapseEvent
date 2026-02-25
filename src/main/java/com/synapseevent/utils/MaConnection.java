package com.synapseevent.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnection {
    private static MaConnection instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/synapse_event";
    private final String user = "root";
    private final String password = "";

    private MaConnection() {
        try {
            // optional but helpful
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            connection = null;
            System.err.println("DB connection failed: " + e.getMessage());
        }
    }

    public static MaConnection getInstance() {
        if (instance == null) instance = new MaConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection requireConnection() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection unavailable. Check MySQL + credentials + DB name (synapse_event).");
        }
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}