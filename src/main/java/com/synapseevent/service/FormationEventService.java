package com.synapseevent.service;

import com.synapseevent.entities.FormationEvent;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class FormationEventService implements IService<FormationEvent> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(FormationEvent event) throws SQLException {
        String sql = "INSERT INTO FormationEvent (name, date, description, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getStatus());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<FormationEvent> readAll() throws SQLException {
        List<FormationEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM FormationEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FormationEvent event = new FormationEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"), rs.getString("status"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    @Override
    public FormationEvent findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM FormationEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new FormationEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"), rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(FormationEvent event) throws SQLException {
        String sql = "UPDATE FormationEvent SET name = ?, date = ?, description = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getStatus());
            stmt.setLong(5, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(FormationEvent event) throws SQLException {
        if (event.getId() != null) {
            String sql = "DELETE FROM FormationEvent WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, event.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<FormationEvent> getPublishedEvents() throws SQLException {
        List<FormationEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM FormationEvent WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FormationEvent event = new FormationEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"), rs.getString("status"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }
}