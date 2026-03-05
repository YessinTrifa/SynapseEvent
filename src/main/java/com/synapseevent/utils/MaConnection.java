package com.synapseevent.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnection {
    private static MaConnection instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/synapse_event?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final String user = "root";
    private final String password = "";

    private MaConnection() {
        connect();
    }
    
    private void connect() {
        try {
            // optional but helpful
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (connection != null && !connection.isClosed()) {
                return; // Already connected
            }
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
        // Try to reconnect if connection is closed or null
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            // Try to reconnect
            connect();
        }
        return connection;
    }

    public Connection requireConnection() throws SQLException {
        // Try to reconnect if connection is closed or null
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
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