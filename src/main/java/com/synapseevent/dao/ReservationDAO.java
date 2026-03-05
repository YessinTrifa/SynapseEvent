package com.synapseevent.dao;

import com.synapseevent.utils.MaConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationDAO {
    
    public boolean reserve(int eventId, int userId, int seats) {
        return reserve(eventId, userId, seats, "PADDLE");
    }
    
    public boolean reserve(int eventId, int userId, int seats, String eventType) {
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            cnx.setAutoCommit(false);
            
            // 1. Vérifier la disponibilité selon le type d'événement
            String checkSql;
            switch (eventType.toUpperCase()) {
                case "PADDLE":
                    checkSql = """
                        SELECT p.capacity - COALESCE(SUM(r.seats), 0) as available
                        FROM PaddleEvent p
                        LEFT JOIN reservations r ON p.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'PADDLE'
                        WHERE p.id = ? AND p.status = 'published'
                        GROUP BY p.capacity
                    """;
                    break;
                case "FORMATION":
                    checkSql = """
                        SELECT f.capacity - COALESCE(SUM(r.seats), 0) as available
                        FROM FormationEvent f
                        LEFT JOIN reservations r ON f.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'FORMATION'
                        WHERE f.id = ? AND f.status = 'published'
                        GROUP BY f.capacity
                    """;
                    break;
                case "TEAMBUILDING":
                    checkSql = """
                        SELECT t.capacity - COALESCE(SUM(r.seats), 0) as available
                        FROM TeamBuildingEvent t
                        LEFT JOIN reservations r ON t.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'TEAMBUILDING'
                        WHERE t.id = ? AND t.status = 'published'
                        GROUP BY t.capacity
                    """;
                    break;
                case "PARTYING":
                    checkSql = """
                        SELECT p.capacity - COALESCE(SUM(r.seats), 0) as available
                        FROM PartyingEvent p
                        LEFT JOIN reservations r ON p.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'PARTYING'
                        WHERE p.id = ? AND p.status = 'published'
                        GROUP BY p.capacity
                    """;
                    break;
                case "ANNIVERSARY":
                    checkSql = """
                        SELECT a.capacity - COALESCE(SUM(r.seats), 0) as available
                        FROM AnniversaryEvent a
                        LEFT JOIN reservations r ON a.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'ANNIVERSARY'
                        WHERE a.id = ? AND a.status = 'published'
                        GROUP BY a.capacity
                    """;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported event type: " + eventType);
            }
            
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
            
            // 2. Insérer la réservation
            String insertSql = """
                INSERT INTO reservations (event_id, user_id, seats, status, event_type, created_at)
                VALUES (?, ?, ?, 'CONFIRMED', ?, NOW())
            """;
            
            try (PreparedStatement insertStmt = cnx.prepareStatement(insertSql)) {
                insertStmt.setInt(1, eventId);
                insertStmt.setInt(2, userId);
                insertStmt.setInt(3, seats);
                insertStmt.setString(4, eventType.toUpperCase());
                
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
}
