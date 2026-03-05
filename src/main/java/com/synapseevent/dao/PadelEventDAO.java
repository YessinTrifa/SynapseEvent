package com.synapseevent.dao;

import com.synapseevent.entities.Event;
import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PadelEventDAO {
    
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
    
    public Event findById(int id) {
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
}
