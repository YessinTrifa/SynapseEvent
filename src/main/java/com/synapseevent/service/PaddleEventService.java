package com.synapseevent.service;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class PaddleEventService implements IService<PaddleEvent> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(PaddleEvent event) throws SQLException {
        String sql = "INSERT INTO PaddleEvent (name, date, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getDescription());
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
    public List<PaddleEvent> readAll() throws SQLException {
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PaddleEvent event = new PaddleEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    @Override
    public PaddleEvent findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM PaddleEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PaddleEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(PaddleEvent event) throws SQLException {
        String sql = "UPDATE PaddleEvent SET name = ?, date = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getDescription());
            stmt.setLong(4, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(PaddleEvent event) throws SQLException {
        if (event.getId() != null) {
            String sql = "DELETE FROM PaddleEvent WHERE id = ?";
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
}