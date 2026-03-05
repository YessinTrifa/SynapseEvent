package com.synapseevent.debug;

import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleDebug {
    
    public static void main(String[] args) {
        System.out.println("=== SIMPLE DEBUG TEAM BUILDING ===");
        
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.out.println("ERROR: Database connection failed");
                return;
            }
            
            // 1. Check if table exists and count records
            System.out.println("\n1. Checking TeamBuildingEvent table:");
            try (Statement stmt = cnx.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM TeamBuildingEvent")) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("   Total records: " + total);
                    
                    if (total == 0) {
                        System.out.println("   TABLE IS EMPTY - Need to insert data!");
                        insertTestData(cnx);
                    } else {
                        System.out.println("   Table has data, checking content...");
                        checkTableContent(cnx);
                    }
                }
            }
            
            // 2. Test the main query
            System.out.println("\n2. Testing main query:");
            testMainQuery(cnx);
            
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void insertTestData(Connection cnx) throws SQLException {
        System.out.println("\n=== INSERTING TEST DATA ===");
        
        // Insert venues first
        String venueSql = """
            INSERT IGNORE INTO Venue (id, name, type, address, city) VALUES 
            (3, 'Escape Room Center Tunis', 'CLUB', 'Avenue Habib Bourguiba', 'Tunis'),
            (4, 'Sousse Beach Resort', 'BEACH', 'Port El Kantaoui', 'Sousse'),
            (5, 'Culinary Institute Tunis', 'HOTEL', 'Rue du Lac', 'Tunis')
        """;
        
        try (Statement stmt = cnx.createStatement()) {
            int rows = stmt.executeUpdate(venueSql);
            System.out.println("Venues inserted: " + rows);
        }
        
        // Insert TeamBuilding events
        String eventSql = """
            INSERT IGNORE INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) VALUES 
            (1, 'Escape Room Challenge', '2026-03-16', '14:00:00', '18:00:00', 'Escape Room Center Tunis', 15, 75.00, 'published', 'Team building through puzzle solving', 'SynapseEvent Team'),
            (2, 'Beach Olympics', '2026-03-23', '09:00:00', '17:00:00', 'Sousse Beach Resort', 40, 120.00, 'published', 'Outdoor team building games', 'Adventure Plus'),
            (3, 'Cooking Masterclass', '2026-03-30', '10:00:00', '14:00:00', 'Culinary Institute Tunis', 20, 95.00, 'published', 'Collaborative cooking experience', 'Gourmet Team')
        """;
        
        try (Statement stmt = cnx.createStatement()) {
            int rows = stmt.executeUpdate(eventSql);
            System.out.println("TeamBuilding events inserted: " + rows);
        }
        
        System.out.println("Test data insertion completed!");
    }
    
    private static void checkTableContent(Connection cnx) throws SQLException {
        System.out.println("\n=== TABLE CONTENT ===");
        
        String sql = "SELECT id, name, date, status, capacity, price, location FROM TeamBuildingEvent ORDER BY id";
        
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Date: " + rs.getDate("date"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Capacity: " + rs.getInt("capacity"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("---");
            }
        }
    }
    
    private static void testMainQuery(Connection cnx) throws SQLException {
        System.out.println("\n=== TESTING MAIN QUERY ===");
        
        String sql = """
            SELECT t.id, t.name, t.date, t.start_time, t.end_time, 
                   t.location, v.city, v.address, t.capacity, 
                   t.price, t.description, t.status, t.organizer,
                   (t.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
            FROM TeamBuildingEvent t
            LEFT JOIN Venue v ON t.location = v.name
            LEFT JOIN (
                SELECT event_id, SUM(seats) as reserved_seats
                FROM reservations 
                WHERE status = 'CONFIRMED' AND event_type = 'TEAMBUILDING'
                GROUP BY event_id
            ) r ON t.id = r.event_id
            WHERE t.status = 'published' 
            AND t.date >= CURDATE()
            AND (t.capacity - COALESCE(r.reserved_seats, 0)) > 0
            ORDER BY t.date ASC, t.start_time ASC
        """;
        
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("FOUND EVENT: " + rs.getString("name"));
                System.out.println("Date: " + rs.getDate("date"));
                System.out.println("Available seats: " + rs.getInt("available_seats"));
                System.out.println("---");
            }
            
            if (count == 0) {
                System.out.println("NO EVENTS FOUND BY MAIN QUERY");
            } else {
                System.out.println("TOTAL EVENTS FOUND: " + count);
            }
        }
    }
}
