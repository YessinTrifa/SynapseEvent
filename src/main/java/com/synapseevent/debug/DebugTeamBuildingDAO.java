package com.synapseevent.debug;

import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DebugTeamBuildingDAO {
    
    public void debugDatabase() {
        System.out.println("=== DEBUG BASE DE DONNÉES TEAM BUILDING ===\n");
        
        // 1. Vérifier si la table TeamBuildingEvent existe et a des données
        debugTableContent();
        
        // 2. Vérifier la table Venue
        debugVenueTable();
        
        // 3. Vérifier la table reservations
        debugReservationsTable();
        
        // 4. Tester la requête principale
        debugMainQuery();
    }
    
    private void debugTableContent() {
        System.out.println("1. Contenu de la table TeamBuildingEvent:");
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.out.println("❌ Connexion à la base de données échouée");
                return;
            }
            
            // Compter les enregistrements
            String countSql = "SELECT COUNT(*) as total FROM TeamBuildingEvent";
            try (PreparedStatement stmt = cnx.prepareStatement(countSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("   Total enregistrements: " + rs.getInt("total"));
                }
            }
            
            // Afficher les détails
            String sql = "SELECT * FROM TeamBuildingEvent";
            try (PreparedStatement stmt = cnx.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    System.out.println("   ID: " + rs.getInt("id"));
                    System.out.println("   Name: " + rs.getString("name"));
                    System.out.println("   Date: " + rs.getDate("date"));
                    System.out.println("   Status: " + rs.getString("status"));
                    System.out.println("   Capacity: " + rs.getInt("capacity"));
                    System.out.println("   Price: " + rs.getDouble("price"));
                    System.out.println("   Location: " + rs.getString("location"));
                    System.out.println("   ---");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void debugVenueTable() {
        System.out.println("2. Contenu de la table Venue (pour Team Building):");
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            
            String sql = "SELECT * FROM Venue WHERE name LIKE '%Escape%' OR name LIKE '%Beach%' OR name LIKE '%Culinary%' OR name LIKE '%Climbing%' OR name LIKE '%Innovation%'";
            try (PreparedStatement stmt = cnx.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    System.out.println("   ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", City: " + rs.getString("city"));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void debugReservationsTable() {
        System.out.println("3. Contenu de la table reservations (TEAMBUILDING):");
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            
            String sql = "SELECT * FROM reservations WHERE event_type = 'TEAMBUILDING'";
            try (PreparedStatement stmt = cnx.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("   Event ID: " + rs.getInt("event_id") + ", Seats: " + rs.getInt("seats") + ", Status: " + rs.getString("status"));
                }
                if (count == 0) {
                    System.out.println("   Aucune réservation TEAMBUILDING trouvée");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void debugMainQuery() {
        System.out.println("4. Test de la requête principale:");
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            
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
            
            try (PreparedStatement stmt = cnx.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("   ✅ Événement trouvé: " + rs.getString("name") + " (Date: " + rs.getDate("date") + ")");
                }
                if (count == 0) {
                    System.out.println("   ❌ Aucun événement trouvé par la requête principale");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur dans la requête principale: " + e.getMessage());
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        DebugTeamBuildingDAO debug = new DebugTeamBuildingDAO();
        debug.debugDatabase();
    }
}
