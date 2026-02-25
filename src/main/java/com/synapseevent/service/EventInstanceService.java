package com.synapseevent.service;

import com.synapseevent.entities.EventInstance;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventInstanceService implements IService<EventInstance> {
    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(EventInstance eventInstance) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, eventInstance.getName());
            stmt.setDate(2, java.sql.Date.valueOf(eventInstance.getDate()));
            if (eventInstance.getStartTime() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(eventInstance.getStartTime()));
            } else {
                stmt.setNull(3, Types.TIME);
            }
            if (eventInstance.getEndTime() != null) {
                stmt.setTime(4, java.sql.Time.valueOf(eventInstance.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIME);
            }
            stmt.setString(5, eventInstance.getLocation());
            if (eventInstance.getCapacity() != null) {
                stmt.setInt(6, eventInstance.getCapacity());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (eventInstance.getPrice() != null) {
                stmt.setDouble(7, eventInstance.getPrice());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setString(8, eventInstance.getOrganizer());
            stmt.setString(9, eventInstance.getDescription());
            stmt.setString(10, eventInstance.getStatus());
            stmt.setString(11, eventInstance.getType());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                eventInstance.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<EventInstance> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<EventInstance> instances = new ArrayList<>();
        String sql = "SELECT * FROM event_instance";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EventInstance instance = new EventInstance(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getObject("capacity") != null ? rs.getInt("capacity") : null,
                    rs.getObject("price") != null ? rs.getDouble("price") : null,
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("type")
                );
                instances.add(instance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return instances;
    }

    @Override
    public EventInstance findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM event_instance WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new EventInstance(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getObject("capacity") != null ? rs.getInt("capacity") : null,
                    rs.getObject("price") != null ? rs.getDouble("price") : null,
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(EventInstance eventInstance) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE event_instance SET name = ?, date = ?, start_time = ?, end_time = ?, location = ?, capacity = ?, price = ?, organizer = ?, description = ?, status = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventInstance.getName());
            stmt.setDate(2, java.sql.Date.valueOf(eventInstance.getDate()));
            if (eventInstance.getStartTime() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(eventInstance.getStartTime()));
            } else {
                stmt.setNull(3, Types.TIME);
            }
            if (eventInstance.getEndTime() != null) {
                stmt.setTime(4, java.sql.Time.valueOf(eventInstance.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIME);
            }
            stmt.setString(5, eventInstance.getLocation());
            if (eventInstance.getCapacity() != null) {
                stmt.setInt(6, eventInstance.getCapacity());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (eventInstance.getPrice() != null) {
                stmt.setDouble(7, eventInstance.getPrice());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setString(8, eventInstance.getOrganizer());
            stmt.setString(9, eventInstance.getDescription());
            stmt.setString(10, eventInstance.getStatus());
            stmt.setString(11, eventInstance.getType());
            stmt.setLong(12, eventInstance.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(EventInstance eventInstance) throws SQLException {
        Connection conn = db.requireConnection();
        if (eventInstance.getId() != null) {
            String sql = "DELETE FROM event_instance WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, eventInstance.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<EventInstance> findByType(String type) throws SQLException {
        Connection conn = db.requireConnection();
        List<EventInstance> instances = new ArrayList<>();
        String sql = "SELECT * FROM event_instance WHERE type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EventInstance instance = new EventInstance(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getObject("capacity") != null ? rs.getInt("capacity") : null,
                    rs.getObject("price") != null ? rs.getDouble("price") : null,
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("type")
                );
                instances.add(instance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return instances;
    }

    public List<EventInstance> getPublishedEvents() throws SQLException {
        Connection conn = db.requireConnection();
        List<EventInstance> instances = new ArrayList<>();
        String sql = "SELECT * FROM event_instance WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EventInstance instance = new EventInstance(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getObject("capacity") != null ? rs.getInt("capacity") : null,
                    rs.getObject("price") != null ? rs.getDouble("price") : null,
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("type")
                );
                instances.add(instance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return instances;
    }
}
