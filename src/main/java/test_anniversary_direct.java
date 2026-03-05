import java.sql.*;

public class test_anniversary_direct {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/synapse_event";
        String user = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            
            // 1. Vérifier si la table existe
            System.out.println("=== VÉRIFICATION TABLE ===");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "AnniversaryEvent", null);
            if (tables.next()) {
                System.out.println("✅ Table AnniversaryEvent existe");
            } else {
                System.out.println("❌ Table AnniversaryEvent n'existe PAS");
                return;
            }
            
            // 2. Compter les enregistrements
            System.out.println("\n=== COMPTAGE ENREGISTREMENTS ===");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM AnniversaryEvent");
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Total enregistrements: " + total);
                
                if (total == 0) {
                    System.out.println("❌ Table vide - insertion nécessaire");
                    
                    // Insertion simple
                    String insertSql = "INSERT IGNORE INTO AnniversaryEvent (name, date, start_time, end_time, location, capacity, price, organizer, description, status, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    
                    pstmt.setString(1, "Test Anniversary Event");
                    pstmt.setDate(2, Date.valueOf("2026-03-20"));
                    pstmt.setTime(3, Time.valueOf("19:00:00"));
                    pstmt.setTime(4, Time.valueOf("23:00:00"));
                    pstmt.setString(5, "Test Location");
                    pstmt.setInt(6, 50);
                    pstmt.setDouble(7, 100.00);
                    pstmt.setString(8, "Test Organizer");
                    pstmt.setString(9, "Test description");
                    pstmt.setString(10, "published");
                    pstmt.setString(11, "Test Category");
                    
                    int rows = pstmt.executeUpdate();
                    System.out.println("✅ Insertion de " + rows + " enregistrement(s)");
                }
            }
            
            // 3. Test de la requête principale
            System.out.println("\n=== TEST REQUÊTE PRINCIPALE ===");
            String mainQuery = """
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
            
            rs = stmt.executeQuery(mainQuery);
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("✅ Événement trouvé: " + rs.getString("name") + 
                                 " (ID: " + rs.getInt("id") + 
                                 ", Date: " + rs.getDate("date") + 
                                 ", Available: " + rs.getInt("available_seats") + ")");
            }
            
            if (count == 0) {
                System.out.println("❌ Aucun événement trouvé par la requête principale");
            } else {
                System.out.println("✅ Total événements trouvés: " + count);
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
