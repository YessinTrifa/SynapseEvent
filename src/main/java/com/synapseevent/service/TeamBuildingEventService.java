package com.synapseevent.service;

import com.synapseevent.entities.EventInstance;
import com.synapseevent.entities.TeamBuildingEvent;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamBuildingEventService implements IService<TeamBuildingEvent> {

    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(TeamBuildingEvent event) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "INSERT INTO TeamBuildingEvent " +
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
    public boolean ajouterWithId(EventInstance ei) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO event_instance (id, name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ei.getId());
            stmt.setString(2, ei.getName());
            stmt.setDate(3, java.sql.Date.valueOf(ei.getDate()));
            stmt.setTime(4, ei.getStartTime() != null ? java.sql.Time.valueOf(ei.getStartTime()) : null);
            stmt.setTime(5, ei.getEndTime() != null ? java.sql.Time.valueOf(ei.getEndTime()) : null);
            stmt.setString(6, ei.getLocation());
            if (ei.getCapacity() != null) stmt.setInt(7, ei.getCapacity()); else stmt.setNull(7, Types.INTEGER);
            if (ei.getPrice() != null) stmt.setDouble(8, ei.getPrice()); else stmt.setNull(8, Types.DOUBLE);
            stmt.setString(9, ei.getOrganizer());
            stmt.setString(10, ei.getDescription());
            stmt.setString(11, ei.getStatus());
            stmt.setString(12, ei.getType());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TeamBuildingEvent> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<TeamBuildingEvent> events = new ArrayList<>();

        String sql = "SELECT * FROM TeamBuildingEvent";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(map(rs));
            }
        }

        return events;
    }

    @Override
    public TeamBuildingEvent findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "SELECT * FROM TeamBuildingEvent WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    @Override
    public boolean modifier(TeamBuildingEvent event) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "UPDATE TeamBuildingEvent SET " +
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
    public boolean supprimer(TeamBuildingEvent event) throws SQLException {
        Connection conn = db.requireConnection();
        if (event == null || event.getId() == null) return false;

        String sql = "DELETE FROM TeamBuildingEvent WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    public List<TeamBuildingEvent> getPublishedEvents() throws SQLException {
        Connection conn = db.requireConnection();
        List<TeamBuildingEvent> events = new ArrayList<>();

        String sql = "SELECT * FROM TeamBuildingEvent WHERE status = 'published'";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(map(rs));
            }
        }

        return events;
    }

    private TeamBuildingEvent map(ResultSet rs) throws SQLException {

        Integer capacity = (Integer) rs.getObject("capacity");
        Double price = (Double) rs.getObject("price");

        Time st = rs.getTime("start_time");
        Time et = rs.getTime("end_time");

        return new TeamBuildingEvent(
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