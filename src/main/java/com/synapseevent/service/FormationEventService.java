package com.synapseevent.service;

import com.synapseevent.entities.FormationEvent;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormationEventService implements IService<FormationEvent> {

    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(FormationEvent event) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "INSERT INTO FormationEvent " +
                "(name, date, start_time, end_time, location, capacity, price, organizer, description, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getName());
            stmt.setDate(2, event.getDate() != null ? Date.valueOf(event.getDate()) : null);
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());

            if (event.getCapacity() == null) stmt.setNull(6, Types.INTEGER);
            else stmt.setInt(6, event.getCapacity());

            if (event.getPrice() == null) stmt.setNull(7, Types.DOUBLE);
            else stmt.setDouble(7, event.getPrice());

            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getDescription());
            stmt.setString(10, event.getStatus());

            int res = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) event.setId(rs.getLong(1));
            }

            return res > 0;
        }
    }

    @Override
    public List<FormationEvent> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<FormationEvent> events = new ArrayList<>();

        String sql = "SELECT * FROM FormationEvent";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) events.add(map(rs));
        }

        return events;
    }

    @Override
    public FormationEvent findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "SELECT * FROM FormationEvent WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    @Override
    public boolean modifier(FormationEvent event) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "UPDATE FormationEvent SET " +
                "name = ?, date = ?, start_time = ?, end_time = ?, location = ?, capacity = ?, price = ?, organizer = ?, description = ?, status = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (event.getId() == null) throw new SQLException("id is required");

            stmt.setString(1, event.getName());
            stmt.setDate(2, event.getDate() != null ? Date.valueOf(event.getDate()) : null);
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());

            if (event.getCapacity() == null) stmt.setNull(6, Types.INTEGER);
            else stmt.setInt(6, event.getCapacity());

            if (event.getPrice() == null) stmt.setNull(7, Types.DOUBLE);
            else stmt.setDouble(7, event.getPrice());

            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getDescription());
            stmt.setString(10, event.getStatus());

            stmt.setLong(11, event.getId());

            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean supprimer(FormationEvent event) throws SQLException {
        Connection conn = db.requireConnection();
        if (event == null || event.getId() == null) return false;

        String sql = "DELETE FROM FormationEvent WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    public List<FormationEvent> getPublishedEvents() throws SQLException {
        Connection conn = db.requireConnection();
        List<FormationEvent> events = new ArrayList<>();

        String sql = "SELECT * FROM FormationEvent WHERE status = 'published'";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) events.add(map(rs));
        }

        return events;
    }

    private FormationEvent map(ResultSet rs) throws SQLException {

        Integer capacity = (Integer) rs.getObject("capacity");
        Double price = (Double) rs.getObject("price");

        Time st = rs.getTime("start_time");
        Time et = rs.getTime("end_time");

        return new FormationEvent(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                st != null ? st.toLocalTime() : null,
                et != null ? et.toLocalTime() : null,
                rs.getString("location"),
                capacity,
                price,
                rs.getString("organizer"),
                rs.getString("description"),
                rs.getString("status")
        );
    }
}