package com.synapseevent.dao;

import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private Connection conn = MaConnection.getInstance().getConnection();

    public void create(Event event) {
        String sql = "INSERT INTO Evenement (nom, type, description, prixBase, capaciteMax) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getNom());
            stmt.setString(2, event.getType());
            stmt.setString(3, event.getDescription());
            stmt.setBigDecimal(4, event.getPrixBase());
            stmt.setInt(5, event.getCapaciteMax());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> readAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Evenement";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event event = new Event(rs.getLong("id"), rs.getString("nom"), rs.getString("type"), rs.getString("description"), rs.getBigDecimal("prixBase"), rs.getInt("capaciteMax"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Event readById(Long id) {
        String sql = "SELECT * FROM Evenement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event(rs.getLong("id"), rs.getString("nom"), rs.getString("type"), rs.getString("description"), rs.getBigDecimal("prixBase"), rs.getInt("capaciteMax"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Event event) {
        String sql = "UPDATE Evenement SET nom = ?, type = ?, description = ?, prixBase = ?, capaciteMax = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getNom());
            stmt.setString(2, event.getType());
            stmt.setString(3, event.getDescription());
            stmt.setBigDecimal(4, event.getPrixBase());
            stmt.setInt(5, event.getCapaciteMax());
            stmt.setLong(6, event.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Evenement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}