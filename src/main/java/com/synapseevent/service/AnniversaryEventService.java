package com.synapseevent.service;

import com.synapseevent.entities.AnniversaryEvent;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class AnniversaryEventService implements IService<AnniversaryEvent> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(AnniversaryEvent event) throws SQLException {
        String sql = "INSERT INTO AnniversaryEvent (name, date, description) VALUES (?, ?, ?)";
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
    public List<AnniversaryEvent> readAll() throws SQLException {
        List<AnniversaryEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM AnniversaryEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AnniversaryEvent event = new AnniversaryEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    @Override
    public AnniversaryEvent findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM AnniversaryEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AnniversaryEvent(rs.getLong("id"), rs.getString("name"), rs.getDate("date").toLocalDate(), rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(AnniversaryEvent event) throws SQLException {
        String sql = "UPDATE AnniversaryEvent SET name = ?, date = ?, description = ? WHERE id = ?";
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
    public boolean supprimer(AnniversaryEvent event) throws SQLException {
        if (event.getId() != null) {
            String sql = "DELETE FROM AnniversaryEvent WHERE id = ?";
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