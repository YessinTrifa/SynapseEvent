import java.sql.*;

public class BasicConnectionTest {
    
    // Modifiez ces valeurs selon votre configuration
    private static final String URL = "jdbc:mysql://localhost:3306/synapse_event";
    private static final String USER = "root";  // Changez selon votre config
    private static final String PASSWORD = "";    // Changez selon votre config
    
    public static void main(String[] args) {
        System.out.println("=== BASIC CONNECTION TEST ===");
        
        Connection conn = null;
        try {
            // 1. Test de connexion
            System.out.println("1. Testing database connection...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connection successful!");
            
            // 2. Vérifier les tables
            System.out.println("\n2. Checking tables...");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "TeamBuildingEvent", null);
            
            if (tables.next()) {
                System.out.println("✅ TeamBuildingEvent table exists!");
            } else {
                System.out.println("❌ TeamBuildingEvent table does NOT exist!");
                return;
            }
            
            // 3. Compter les enregistrements
            System.out.println("\n3. Counting records...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM TeamBuildingEvent");
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("📊 Total records in TeamBuildingEvent: " + count);
                
                if (count == 0) {
                    System.out.println("⚠️  Table is empty - inserting test data...");
                    
                    // Insérer des données de test
                    String insertSql = "INSERT INTO TeamBuildingEvent (name, date, start_time, end_time, location, capacity, price, status, description, organizer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    
                    pstmt.setString(1, "Test Event");
                    pstmt.setDate(2, Date.valueOf("2026-03-20"));
                    pstmt.setTime(3, Time.valueOf("09:00:00"));
                    pstmt.setTime(4, Time.valueOf("17:00:00"));
                    pstmt.setString(5, "Test Location");
                    pstmt.setInt(6, 20);
                    pstmt.setDouble(7, 100.00);
                    pstmt.setString(8, "published");
                    pstmt.setString(9, "Test description");
                    pstmt.setString(10, "Test Organizer");
                    
                    int rows = pstmt.executeUpdate();
                    System.out.println("✅ Inserted " + rows + " test record(s)");
                }
            }
            
            // 4. Tester la requête principale
            System.out.println("\n4. Testing main query...");
            String mainQuery = """
                SELECT t.id, t.name, t.date, t.start_time, t.end_time, 
                       t.location, t.capacity, t.price, t.description, t.status, t.organizer,
                       (t.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
                FROM TeamBuildingEvent t
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
            
            rs = stmt.executeQuery(mainQuery);
            int eventCount = 0;
            
            while (rs.next()) {
                eventCount++;
                System.out.println("🎯 Found event: " + rs.getString("name") + 
                                 " (Date: " + rs.getDate("date") + 
                                 ", Available: " + rs.getInt("available_seats") + " seats)");
            }
            
            if (eventCount == 0) {
                System.out.println("❌ No events found by main query");
            } else {
                System.out.println("✅ Found " + eventCount + " event(s)!");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("❌ Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
