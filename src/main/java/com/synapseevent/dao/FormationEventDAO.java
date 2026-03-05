package com.synapseevent.dao;

import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FormationEventDAO {
    
    public List<Event> findFormationEventsAvailable() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT f.id, f.name, f.date, f.start_time, f.end_time, 
                   f.location, v.city, v.address, f.capacity, 
                   f.price, f.description, f.status, f.organizer,
                   (f.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM FormationEvent f
            LEFT JOIN Venue v ON f.location = v.name
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'FORMATION'
                GROUP BY event_id
            ) r ON f.id = r.event_id
            WHERE f.status = 'published' 
            AND f.date >= CURDATE()
            AND (f.capacity - COALESCE(r.reserved_seats, 0)) > 0
            ORDER BY f.date ASC, f.start_time ASC
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
                    event.setType("FORMATION");
                    event.setDate(rs.getDate("date").toLocalDate());
                    event.setStartTime(rs.getTime("start_time").toLocalTime());
                    event.setEndTime(rs.getTime("end_time").toLocalTime());
                    event.setLocation(rs.getString("location"));
                    event.setCity(rs.getString("city"));
                    event.setAddress(rs.getString("address"));
                    event.setCapacity(rs.getInt("available_seats"));
                    event.setPrice(rs.getDouble("price"));
                    event.setDescription(rs.getString("description"));
                    event.setStatus(rs.getString("status"));
                    event.setOrganizer(rs.getString("organizer"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available formation events: " + e.getMessage());
        }
        return events;
    }
    
    public Event findById(int id) {
        String sql = """
            SELECT f.id, f.name, f.date, f.start_time, f.end_time, 
                   f.location, v.city, v.address, f.capacity, 
                   f.price, f.description, f.status, f.organizer,
                   (f.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM FormationEvent f
            LEFT JOIN Venue v ON f.location = v.name
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'FORMATION'
                GROUP BY event_id
            ) r ON f.id = r.event_id
            WHERE f.id = ? AND f.status = 'published'
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
                        event.setType("FORMATION");
                        event.setDate(rs.getDate("date").toLocalDate());
                        event.setStartTime(rs.getTime("start_time").toLocalTime());
                        event.setEndTime(rs.getTime("end_time").toLocalTime());
                        event.setLocation(rs.getString("location"));
                        event.setCity(rs.getString("city"));
                        event.setAddress(rs.getString("address"));
                        event.setCapacity(rs.getInt("available_seats"));
                        event.setPrice(rs.getDouble("price"));
                        event.setDescription(rs.getString("description"));
                        event.setStatus(rs.getString("status"));
                        event.setOrganizer(rs.getString("organizer"));
                        return event;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding formation event by id: " + e.getMessage());
        }
        return null;
    }
    
    public int getReservedConfirmedSeats(int eventId) {
        String sql = """
            SELECT COALESCE(SUM(seats), 0) as reserved_seats
            FROM reservations 
            WHERE event_id = ? AND status = 'CONFIRMED' AND event_type = 'FORMATION'
        """;
        
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                return 0;
            }
            
            try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("reserved_seats");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reserved seats: " + e.getMessage());
        }
        return 0;
    }
    
    public int getTotalCapacity(int eventId) {
        String sql = "SELECT capacity FROM FormationEvent WHERE id = ?";
        
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                return 0;
            }
            
            try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("capacity");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total capacity: " + e.getMessage());
        }
        return 0;
    }
}
