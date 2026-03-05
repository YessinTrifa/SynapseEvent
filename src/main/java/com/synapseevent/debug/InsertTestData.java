package com.synapseevent.debug;

import com.synapseevent.utils.MaConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe pour insérer les données de test Team Building si elles n'existent pas
 */
public class InsertTestData {
    
    public static void main(String[] args) {
        System.out.println("=== Insertion des données de test Team Building ===\n");
        
        try {
            Connection cnx = MaConnection.getInstance().getConnection();
            if (cnx == null || cnx.isClosed()) {
                System.out.println("❌ Connexion à la base de données échouée");
                return;
            }
            
            // 1. Insérer les venues d'abord
            insertVenues(cnx);
            
            // 2. Insérer les événements Team Building
            insertTeamBuildingEvents(cnx);
            
            // 3. Vérifier l'insertion
            verifyInsertion(cnx);
            
            System.out.println("✅ Données de test insérées avec succès !");
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void insertVenues(Connection cnx) throws SQLException {
        System.out.println("1. Insertion des venues...");
        
        String venueSql = """
            INSERT IGNORE INTO Venue (id, name, type, address, city) VALUES 
            (3, 'Escape Room Center Tunis', 'CLUB', 'Avenue Habib Bourguiba, Centre Commercial', 'Tunis'),
            (4, 'Sousse Beach Resort', 'BEACH', 'Bord de mer, Port El Kantaoui', 'Sousse'),
            (5, 'Culinary Institute Tunis', 'HOTEL', 'Rue du Lac, Les Berges du Lac', 'Tunis'),
            (6, 'Indoor Climbing Gym Tunis', 'CLUB', 'Zone Industrielle, El Menzah', 'Tunis'),
            (7, 'Innovation Lab Tunis', 'HOTEL', 'Avenue de la République', 'Tunis')
        """;
        
        try (PreparedStatement stmt = cnx.prepareStatement(venueSql)) {
            int rows = stmt.executeUpdate();
            System.out.println("   ✅ " + rows + " venues insérées/ignorées");
        }
    }
    
    private static void insertTeamBuildingEvents(Connection cnx) throws SQLException {
        System.out.println("2. Insertion des événements Team Building...");
        
        String eventSql = """
            INSERT IGNORE INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) VALUES 
            (1, 'Escape Room Challenge', '2026-03-16', '14:00:00', '18:00:00', 'Escape Room Center Tunis', 15, 75.00, 'published', 'Team building through puzzle solving and collaboration', 'SynapseEvent Team'),
            (2, 'Beach Olympics', '2026-03-23', '09:00:00', '17:00:00', 'Sousse Beach Resort', 40, 120.00, 'published', 'Outdoor team building games and competitions', 'Adventure Plus'),
            (3, 'Cooking Masterclass', '2026-03-30', '10:00:00', '14:00:00', 'Culinary Institute Tunis', 20, 95.00, 'published', 'Collaborative cooking experience with professional chefs', 'Gourmet Team'),
            (4, 'Rock Climbing Adventure', '2026-04-06', '08:00:00', '16:00:00', 'Indoor Climbing Gym Tunis', 25, 110.00, 'published', 'Trust building through rock climbing challenges', 'Extreme Sports Co'),
            (5, 'Innovation Workshop', '2026-04-13', '09:00:00', '17:00:00', 'Innovation Lab Tunis', 30, 180.00, 'published', 'Creative problem solving and design thinking exercises', 'Innovation Hub')
        """;
        
        try (PreparedStatement stmt = cnx.prepareStatement(eventSql)) {
            int rows = stmt.executeUpdate();
            System.out.println("   ✅ " + rows + " événements Team Building insérés/ignorés");
        }
    }
    
    private static void verifyInsertion(Connection cnx) throws SQLException {
        System.out.println("3. Vérification de l'insertion...");
        
        // Compter les événements
        String countSql = "SELECT COUNT(*) as total FROM TeamBuildingEvent WHERE status = 'published'";
        try (PreparedStatement stmt = cnx.prepareStatement(countSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("   📊 Total événements publiés: " + rs.getInt("total"));
            }
        }
        
        // Afficher les événements disponibles
        String sql = """
            SELECT t.id, t.name, t.date, t.location, t.capacity, t.price, t.status
            FROM TeamBuildingEvent t
            WHERE t.status = 'published' 
            AND t.date >= CURDATE()
            ORDER BY t.date ASC
        """;
        
        try (PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("   📋 Événements disponibles:");
            while (rs.next()) {
                System.out.println("      • " + rs.getString("name") + 
                                 " (" + rs.getDate("date") + ") - " + 
                                 rs.getInt("capacity") + " places - " + 
                                 rs.getDouble("price") + " TND");
            }
        }
    }
}
