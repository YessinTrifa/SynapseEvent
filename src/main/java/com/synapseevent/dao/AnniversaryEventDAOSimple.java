package com.synapseevent.dao;

import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AnniversaryEventDAOSimple {
    
    public List<Event> findAnniversaryEventsAvailable() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT a.id, a.name, a.date, a.start_time, a.end_time, 
                   a.location, a.capacity, 
                   a.price, a.description, a.status, a.organizer,
                   (a.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM AnniversaryEvent a
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'ANNIVERSARY'
                GROUP BY event_id
            ) r ON a.id = r.event_id
            WHERE a.status = 'published' 
            AND a.date >= CURDATE()
            AND (a.capacity - COALESCE(r.reserved_seats, 0)) > 0
            ORDER BY a.date ASC, a.start_time ASC
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
                    event.setType("ANNIVERSARY");
                    event.setDate(rs.getDate("date").toLocalDate());
                    event.setStartTime(rs.getTime("start_time").toLocalTime());
                    event.setEndTime(rs.getTime("end_time").toLocalTime());
                    event.setLocation(rs.getString("location"));
                    // Pas de city et address dans la table AnniversaryEvent
                    event.setCity(rs.getString("location"));
                    event.setAddress(rs.getString("location"));
                    event.setCapacity(rs.getInt("available_seats"));
                    event.setPrice(rs.getDouble("price"));
                    event.setDescription(rs.getString("description"));
                    event.setStatus(rs.getString("status"));
                    event.setOrganizer(rs.getString("organizer"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available anniversary events: " + e.getMessage());
        }
        return events;
    }
    
    public Event findById(int id) {
        String sql = """
            SELECT a.id, a.name, a.date, a.start_time, a.end_time, 
                   a.location, a.capacity, 
                   a.price, a.description, a.status, a.organizer,
                   (a.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM AnniversaryEvent a
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'ANNIVERSARY'
                GROUP BY event_id
            ) r ON a.id = r.event_id
            WHERE a.id = ? AND a.status = 'published'
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
                        event.setType("ANNIVERSARY");
                        event.setDate(rs.getDate("date").toLocalDate());
                        event.setStartTime(rs.getTime("start_time").toLocalTime());
                        event.setEndTime(rs.getTime("end_time").toLocalTime());
                        event.setLocation(rs.getString("location"));
                        // Pas de city et address dans la table AnniversaryEvent
                        event.setCity(rs.getString("location"));
                        event.setAddress(rs.getString("location"));
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
            System.err.println("Error finding anniversary event by id: " + e.getMessage());
        }
        return null;
    }
}
