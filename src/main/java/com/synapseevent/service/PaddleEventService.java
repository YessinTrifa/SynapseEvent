package com.synapseevent.service;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class PaddleEventService implements IService<PaddleEvent> {
    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(PaddleEvent event) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO PaddleEvent (name, date, start_time, end_time, location, map, capacity, reservation, price, disponibilite, organizer, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getMap());
            stmt.setInt(7, event.getCapacity() != null ? event.getCapacity() : 0);
            stmt.setInt(8, event.getReservation() != null ? event.getReservation() : 0);
            stmt.setDouble(9, event.getPrice() != null ? event.getPrice() : 0.0);
            stmt.setBoolean(10, event.getDisponibilite() != null ? event.getDisponibilite() : true);
            stmt.setString(11, event.getOrganizer());
            stmt.setString(12, event.getDescription());
            stmt.setString(13, event.getStatus());
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
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PaddleEvent event = new PaddleEvent(
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getString("map"),
                    rs.getInt("capacity"),
                    rs.getInt("reservation"),
                    rs.getDouble("price"),
                    rs.getBoolean("disponibilite"),
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                event.setId(rs.getLong("id"));
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
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM PaddleEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PaddleEvent event = new PaddleEvent(
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getString("map"),
                    rs.getInt("capacity"),
                    rs.getInt("reservation"),
                    rs.getDouble("price"),
                    rs.getBoolean("disponibilite"),
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                event.setId(rs.getLong("id"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(PaddleEvent event) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE PaddleEvent SET name = ?, date = ?, start_time = ?, end_time = ?, location = ?, map = ?, capacity = ?, reservation = ?, price = ?, disponibilite = ?, organizer = ?, description = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getMap());
            stmt.setInt(7, event.getCapacity() != null ? event.getCapacity() : 0);
            stmt.setInt(8, event.getReservation() != null ? event.getReservation() : 0);
            stmt.setDouble(9, event.getPrice() != null ? event.getPrice() : 0.0);
            stmt.setBoolean(10, event.getDisponibilite() != null ? event.getDisponibilite() : true);
            stmt.setString(11, event.getOrganizer());
            stmt.setString(12, event.getDescription());
            stmt.setString(13, event.getStatus());
            stmt.setLong(14, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(PaddleEvent event) throws SQLException {
        Connection conn = db.requireConnection();
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

    public List<PaddleEvent> getPublishedEvents() throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PaddleEvent event = new PaddleEvent(
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getString("map"),
                    rs.getInt("capacity"),
                    rs.getInt("reservation"),
                    rs.getDouble("price"),
                    rs.getBoolean("disponibilite"),
                    rs.getString("organizer"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                event.setId(rs.getLong("id"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    // Advanced search and filter methods
    public List<PaddleEvent> searchByName(String name) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE name LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PaddleEvent> searchByLocation(String location) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE location LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + location + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PaddleEvent> filterByStatus(String status) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PaddleEvent> filterByAvailability(boolean available) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE disponibilite = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PaddleEvent> filterByPriceRange(double minPrice, double maxPrice) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE price BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PaddleEvent> filterByCapacityRange(int minCapacity, int maxCapacity) throws SQLException {
        Connection conn = db.requireConnection();
        List<PaddleEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PaddleEvent WHERE capacity BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, minCapacity);
            stmt.setInt(2, maxCapacity);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public int getTotalCapacity() throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT SUM(capacity) as total FROM PaddleEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0;
    }

    public int getTotalReservations() throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT SUM(reservation) as total FROM PaddleEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0;
    }

    public double getTotalRevenue() throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT SUM(price) as total FROM PaddleEvent WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0.0;
    }

    public long getAvailableEventsCount() throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT COUNT(*) as count FROM PaddleEvent WHERE disponibilite = true";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0;
    }

    private PaddleEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        Connection conn = db.requireConnection();
        PaddleEvent event = new PaddleEvent(
            rs.getString("name"),
            rs.getDate("date").toLocalDate(),
            rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
            rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
            rs.getString("location"),
            rs.getString("map"),
            rs.getInt("capacity"),
            rs.getInt("reservation"),
            rs.getDouble("price"),
            rs.getBoolean("disponibilite"),
            rs.getString("organizer"),
            rs.getString("description"),
            rs.getString("status")
        );
        event.setId(rs.getLong("id"));
        return event;
    }
}
