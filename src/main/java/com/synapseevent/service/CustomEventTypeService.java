package com.synapseevent.service;

import com.synapseevent.entities.CustomEventType;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomEventTypeService implements IService<CustomEventType> {

    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(CustomEventType type) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO CustomEventType (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                type.setId(rs.getLong(1));
            }
            return res > 0;
        }
    }

    @Override
    public List<CustomEventType> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<CustomEventType> types = new ArrayList<>();
        String sql = "SELECT * FROM CustomEventType";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                types.add(new CustomEventType(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                ));
            }
        }
        return types;
    }

    @Override
    public CustomEventType findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM CustomEventType WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new CustomEventType(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                );
            }
        }
        return null;
    }

    @Override
    public boolean modifier(CustomEventType type) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE CustomEventType SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setLong(3, type.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean supprimer(CustomEventType type) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "DELETE FROM CustomEventType WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, type.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public CustomEventType getByName(String name) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM CustomEventType WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new CustomEventType(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                );
            }
        }
        return null;
    }
}