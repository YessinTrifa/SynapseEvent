package com.synapseevent.service;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class PaddleEventService implements IService<PaddleEvent> {
    private final MaConnection db = MaConnection.getInstance();

    // Additional methods for Event entity (used in reservations)
    public List<Event> findPadelEventsAvailable() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT p.id, p.name, p.date, p.start_time, p.end_time, 
                   p.location, v.city, v.address, p.map, p.capacity, 
                   p.price, p.description, p.status,
                   (p.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM PaddleEvent p
            LEFT JOIN Venue v ON p.location = v.name
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'PADDLE'
                GROUP BY event_id
            ) r ON p.id = r.event_id
            WHERE p.status = 'published' 
            AND p.date >= CURDATE()
            AND (p.capacity - COALESCE(r.reserved_seats, 0)) > 0
            ORDER BY p.date ASC, p.start_time ASC
        """;
        
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.err.println("Database connection is not available");
                return events;
            }
            
            try (PreparedStatement stmt = cnx.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(rs.getLong("id"));
                    event.setName(rs.getString("name"));
                    event.setType("PADDLE");
                    event.setDate(rs.getDate("date").toLocalDate());
                    event.setStartTime(rs.getTime("start_time").toLocalTime());
                    event.setEndTime(rs.getTime("end_time").toLocalTime());
                    event.setLocation(rs.getString("location"));
                    event.setCity(rs.getString("city"));
                    event.setAddress(rs.getString("address"));
                    event.setMap(rs.getString("map"));
                    event.setCapacity(rs.getInt("available_seats"));
                    event.setPrice(rs.getDouble("price"));
                    event.setDescription(rs.getString("description"));
                    event.setStatus(rs.getString("status"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available paddle events: " + e.getMessage());
        }
        return events;
    }
    
    public Event findEventById(int id) {
        String sql = """
            SELECT p.id, p.name, p.date, p.start_time, p.end_time, 
                   p.location, v.city, v.address, p.map, p.capacity, 
                   p.price, p.description, p.status,
                   (p.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM PaddleEvent p
            LEFT JOIN Venue v ON p.location = v.name
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'PADDLE'
                GROUP BY event_id
            ) r ON p.id = r.event_id
            WHERE p.id = ? AND p.status = 'published'
        """;
        
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.err.println("Database connection is not available");
                return null;
            }
            
            try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Event event = new Event();
                        event.setId(rs.getLong("id"));
                        event.setName(rs.getString("name"));
                        event.setType("PADDLE");
                        event.setDate(rs.getDate("date").toLocalDate());
                        event.setStartTime(rs.getTime("start_time").toLocalTime());
                        event.setEndTime(rs.getTime("end_time").toLocalTime());
                        event.setLocation(rs.getString("location"));
                        event.setCity(rs.getString("city"));
                        event.setAddress(rs.getString("address"));
                        event.setMap(rs.getString("map"));
                        event.setCapacity(rs.getInt("available_seats"));
                        event.setPrice(rs.getDouble("price"));
                        event.setDescription(rs.getString("description"));
                        event.setStatus(rs.getString("status"));
                        return event;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding paddle event by id: " + e.getMessage());
        }
        return null;
    }
    
    public int getReservedConfirmedSeats(int eventId) {
        String sql = """
            SELECT COALESCE(SUM(seats), 0) as reserved_seats
            FROM reservations 
            WHERE event_id = ? AND status = 'CONFIRMED' AND event_type = 'PADDLE'
        """;
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("reserved_seats");
            }
        } catch (SQLException e) {
            System.err.println("Error getting reserved seats: " + e.getMessage());
        }
        return 0;
    }
    
    public int getTotalCapacity(int eventId) {
        String sql = "SELECT capacity FROM PaddleEvent WHERE id = ?";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("capacity");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total capacity: " + e.getMessage());
        }
        return 0;
    }
    
    // Reservation method
    public boolean reserve(int eventId, int userId, int seats) {
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            cnx.setAutoCommit(false);
            
            // 1. Check availability
            String checkSql = """
                SELECT p.capacity - COALESCE(SUM(r.seats), 0) as available
                FROM PaddleEvent p
                LEFT JOIN reservations r ON p.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'PADDLE'
                WHERE p.id = ? AND p.status = 'published'
                GROUP BY p.capacity
            """;
            
            try (PreparedStatement checkStmt = cnx.prepareStatement(checkSql)) {
                checkStmt.setInt(1, eventId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int available = rs.getInt("available");
                    if (available < seats) {
                        cnx.rollback();
                        return false;
                    }
                } else {
                    cnx.rollback();
                    return false;
                }
            }
            
            // 2. Insert reservation
            String insertSql = """
                INSERT INTO reservations (event_id, user_id, seats, status, event_type, created_at)
                VALUES (?, ?, ?, 'CONFIRMED', 'PADDLE', NOW())
            """;
            
            try (PreparedStatement insertStmt = cnx.prepareStatement(insertSql)) {
                insertStmt.setInt(1, eventId);
                insertStmt.setInt(2, userId);
                insertStmt.setInt(3, seats);
                
                int rowsAffected = insertStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    cnx.commit();
                    return true;
                } else {
                    cnx.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during reservation: " + e.getMessage());
            try {
                if (cnx != null) cnx.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (cnx != null) cnx.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

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
