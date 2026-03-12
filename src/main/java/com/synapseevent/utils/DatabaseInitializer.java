package com.synapseevent.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        MaConnection maConn = MaConnection.getInstance();
        Connection conn = maConn.getConnection();

        // Check if database connection is available
        if (!maConn.isConnected()) {
            System.err.println("⚠️  Database connection unavailable. Database initialization skipped.");
            System.err.println("   Application will continue, but database features will not work.");
            System.err.println("   Please ensure MySQL is running and configured correctly.");
            return;
        }

        try {
            System.out.println("📦 Initializing database...");
            // First, run migrations to add missing columns to existing tables
            runMigrations(conn);

            // Disable foreign key checks before dropping tables
            Statement stmt = conn.createStatement();
            System.out.println("Disabling foreign key checks...");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            System.out.println("Foreign key checks disabled.");

            // Read the schema.sql file as a single string
            InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql");
            if (is == null) {
                System.err.println("schema.sql not found in resources");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("schema.sql found, length: " + content.length() + " chars");

            // Find all complete statements (ending with ;)
            // Use .+? to match any characters including newlines, followed by ;
            List<String> statements = new ArrayList<>();
            Pattern pattern = Pattern.compile("(.+?;)\\s*", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String matchedStmt = matcher.group(1).trim();
                if (matchedStmt.endsWith(";")) {
                    statements.add(matchedStmt);
                }
            }

            System.out.println("Found " + statements.size() + " statements");

            int count = 0;
            int execCount = 0;
            int skipCount = 0;
            for (String statement : statements) {
                String trimmed = statement.trim();

                // Skip statements that are purely comments (start with -- and no other content)
                if (trimmed.startsWith("--") && !trimmed.contains("\n")) {
                    skipCount++;
                    continue;
                }

                // Skip DROP TABLE statements to preserve existing data
                if (trimmed.toUpperCase().startsWith("DROP TABLE")) {
                    System.out.println("Skipping DROP TABLE statement to preserve data");
                    skipCount++;
                    continue;
                }

                try {
                    String toExecute = trimmed;
                    System.out.println("Executing: " + toExecute.substring(0, Math.min(80, toExecute.length())) + "...");
                    boolean result = stmt.execute(toExecute);
                    System.out.println("Result: " + result);
                    execCount++;
                } catch (Exception e) {
                    System.err.println("Error executing: " + trimmed.substring(0, Math.min(100, trimmed.length())) + "...");
                    System.err.println("Exception: " + e.getMessage());
                }
                count++;
            }
            System.out.println("Processed " + count + " statements, executed " + execCount + " successfully, skipped " + skipCount + " pure comments.");

            // Re-enable foreign key checks after creating tables
            System.out.println("Re-enabling foreign key checks...");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            stmt.close();

            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("⚠️  Database initialization failed: " + e.getMessage());
            System.err.println("   Application will continue without database initialization.");
            System.err.println("   Some features may not work correctly.");
            // Don't crash the application - let it continue without DB
        }
    }

    /**
     * Run migrations to add missing columns to existing tables.
     * This ensures that new schema changes are applied to existing databases.
     */
    private static void runMigrations(Connection conn) {
        System.out.println("Running migrations...");
        try {
            // Venue
            addColumnIfNotExists(conn, "Venue", "city", "VARCHAR(255)");

            // Court table - create if not exists
            createTableIfNotExists(conn, "Court",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY,\n                name VARCHAR(255) NOT NULL,\n                venue_id BIGINT NOT NULL,\n                is_indoor BOOLEAN DEFAULT FALSE,\n                price_per_hour DECIMAL(10,2) NOT NULL,\n                available BOOLEAN DEFAULT TRUE,\n                description TEXT,\n                amenities TEXT,\n                FOREIGN KEY (venue_id) REFERENCES Venue(id) ON DELETE CASCADE");

            // court_reservations table - create if not exists
            createTableIfNotExists(conn, "court_reservations",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY,\n                court_id BIGINT NOT NULL,\n                user_id BIGINT NOT NULL,\n                reservation_date DATE NOT NULL,\n                start_time TIME NOT NULL,\n                end_time TIME NOT NULL,\n                total_price DECIMAL(10,2) NOT NULL,\n                status VARCHAR(20) DEFAULT 'CONFIRMED',\n                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n                FOREIGN KEY (court_id) REFERENCES Court(id) ON DELETE CASCADE,\n                FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE");

            // CustomEventRequest
            addColumnIfNotExists(conn, "CustomEventRequest", "budget", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "CustomEventRequest", "capacity", "INT");
            addColumnIfNotExists(conn, "CustomEventRequest", "location", "VARCHAR(255)");
            addColumnIfNotExists(conn, "CustomEventRequest", "reason", "TEXT");

            // FormationEvent
            addColumnIfNotExists(conn, "FormationEvent", "start_time", "TIME");
            addColumnIfNotExists(conn, "FormationEvent", "end_time", "TIME");
            addColumnIfNotExists(conn, "FormationEvent", "location", "VARCHAR(255)");
            addColumnIfNotExists(conn, "FormationEvent", "capacity", "INT");
            addColumnIfNotExists(conn, "FormationEvent", "price", "DOUBLE");
            addColumnIfNotExists(conn, "FormationEvent", "organizer", "VARCHAR(255)");
            addColumnIfNotExists(conn, "FormationEvent", "description", "TEXT");
            addColumnIfNotExists(conn, "FormationEvent", "status", "VARCHAR(50)");

            // PaddleEvent
            addColumnIfNotExists(conn, "PaddleEvent", "start_time", "TIME");
            addColumnIfNotExists(conn, "PaddleEvent", "end_time", "TIME");
            addColumnIfNotExists(conn, "PaddleEvent", "location", "VARCHAR(255)");
            addColumnIfNotExists(conn, "PaddleEvent", "map", "VARCHAR(500)");
            addColumnIfNotExists(conn, "PaddleEvent", "capacity", "INT");
            addColumnIfNotExists(conn, "PaddleEvent", "reservation", "INT DEFAULT 0");
            addColumnIfNotExists(conn, "PaddleEvent", "price", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "PaddleEvent", "disponibilite", "BOOLEAN DEFAULT TRUE");
            addColumnIfNotExists(conn, "PaddleEvent", "organizer", "VARCHAR(255)");
            addColumnIfNotExists(conn, "PaddleEvent", "description", "TEXT");
            addColumnIfNotExists(conn, "PaddleEvent", "status", "VARCHAR(20) DEFAULT 'draft'");

            // TeamBuildingEvent
            addColumnIfNotExists(conn, "TeamBuildingEvent", "start_time", "TIME");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "end_time", "TIME");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "location", "VARCHAR(255)");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "capacity", "INT");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "price", "DOUBLE");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "organizer", "VARCHAR(255)");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "description", "TEXT");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "status", "VARCHAR(50)");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "is_pack", "BOOLEAN DEFAULT FALSE");
            addColumnIfNotExists(conn, "TeamBuildingEvent", "activities", "TEXT");
            
            // TeamBuildingActivity table for games and activities
            createTableIfNotExists(conn, "TeamBuildingActivity",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "category VARCHAR(100), " +
                "duration_minutes INT, " +
                "price_per_person DECIMAL(10,2), " +
                "min_participants INT DEFAULT 1, " +
                "max_participants INT DEFAULT 100, " +
                "is_active BOOLEAN DEFAULT TRUE");
            
            // Insert TeamBuildingActivity sample data if table is empty
            try {
                ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM TeamBuildingActivity");
                if (rs.next() && rs.getInt("cnt") == 0) {
                    System.out.println("Inserting TeamBuildingActivity sample data...");
                    Statement stmt = conn.createStatement();
                    stmt.execute("INSERT INTO TeamBuildingActivity (name, description, category, duration_minutes, price_per_person, min_participants, max_participants) VALUES " +
                        "('Escape Room Challenge', 'Solve puzzles and escape within time limit', 'Indoor', 60, 25.00, 4, 20), " +
                        "('Laser Tag', 'Team-based laser tag combat game', 'Indoor', 90, 30.00, 10, 30), " +
                        "('Bowling Tournament', 'Competitive bowling with scoring', 'Indoor', 120, 20.00, 8, 40), " +
                        "('Karting Race', 'Go-kart racing competition', 'Outdoor', 60, 35.00, 8, 24), " +
                        "('Treasure Hunt', 'Outdoor adventure with clues and challenges', 'Outdoor', 180, 15.00, 10, 50), " +
                        "('Paintball', 'Team combat with paintball markers', 'Outdoor', 120, 40.00, 10, 30), " +
                        "('Cooking Class', 'Learn to cook with professional chef', 'Culinary', 180, 50.00, 6, 20), " +
                        "('Wine Tasting', 'Tasting and learning about wines', 'Culinary', 90, 45.00, 8, 25), " +
                        "('Team Cooking Challenge', 'Groups compete in cooking challenge', 'Culinary', 150, 55.00, 8, 30), " +
                        "('Outdoor Camping', 'Overnight camping with team activities', 'Adventure', 1440, 80.00, 10, 40), " +
                        "('Hiking Adventure', 'Group hiking with team challenges', 'Adventure', 240, 25.00, 8, 30), " +
                        "('Rafting', 'White water rafting experience', 'Adventure', 180, 60.00, 8, 20), " +
                        "('Trust Falls', 'Classic team building trust exercise', 'Team Building', 60, 0.00, 10, 50), " +
                        "('Problem Solving Games', 'Brain teasers and problem solving', 'Team Building', 90, 10.00, 8, 30), " +
                        "('Corporate Workshop', 'Professional team building workshop', 'Team Building', 240, 35.00, 10, 40)");
                    System.out.println("TeamBuildingActivity sample data inserted.");
                }
            } catch (Exception e) {
                System.out.println("Could not insert TeamBuildingActivity data: " + e.getMessage());
            }

            // AnniversaryEvent
            addColumnIfNotExists(conn, "AnniversaryEvent", "start_time", "TIME");
            addColumnIfNotExists(conn, "AnniversaryEvent", "end_time", "TIME");
            addColumnIfNotExists(conn, "AnniversaryEvent", "location", "VARCHAR(255)");
            addColumnIfNotExists(conn, "AnniversaryEvent", "capacity", "INT");
            addColumnIfNotExists(conn, "AnniversaryEvent", "price", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "AnniversaryEvent", "organizer", "VARCHAR(255)");
            addColumnIfNotExists(conn, "AnniversaryEvent", "category", "VARCHAR(100)");
            addColumnIfNotExists(conn, "AnniversaryEvent", "description", "TEXT");
            addColumnIfNotExists(conn, "AnniversaryEvent", "status", "VARCHAR(20) DEFAULT 'draft'");

            // PartyingEvent
            addColumnIfNotExists(conn, "PartyingEvent", "start_time", "TIME");
            addColumnIfNotExists(conn, "PartyingEvent", "end_time", "TIME");
            addColumnIfNotExists(conn, "PartyingEvent", "venue_id", "BIGINT");
            addColumnIfNotExists(conn, "PartyingEvent", "capacity", "INT");
            addColumnIfNotExists(conn, "PartyingEvent", "price", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "PartyingEvent", "organizer", "VARCHAR(255)");
            addColumnIfNotExists(conn, "PartyingEvent", "description", "TEXT");
            addColumnIfNotExists(conn, "PartyingEvent", "status", "VARCHAR(20) DEFAULT 'draft'");

            // Pricing Engine tables
            runPricingMigrations(conn);

            System.out.println("Migrations completed.");
        } catch (Exception e) {
            System.err.println("Error running migrations: " + e.getMessage());
        }
    }

    private static void runPricingMigrations(Connection conn) {
        System.out.println("Running pricing schema migrations...");
        try {
            createTableIfNotExists(conn, "coupons",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "code VARCHAR(50) NOT NULL UNIQUE, " +
                "type ENUM('PERCENT', 'FIXED') NOT NULL, " +
                "value DECIMAL(10,2) NOT NULL, " +
                "start_date DATE, end_date DATE, " +
                "usage_limit INT, used_count INT DEFAULT 0, " +
                "min_spend DECIMAL(10,2), is_active BOOLEAN DEFAULT TRUE, " +
                "applicable_event_types TEXT, description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            createTableIfNotExists(conn, "pricing_rules",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "event_type VARCHAR(50), " +
                "rule_type ENUM('VOLUME','EARLY_BIRD','OFF_PEAK','GROUP_SIZE','VENUE_BASE') NOT NULL, " +
                "condition_type ENUM('MIN_PEOPLE','EXACT_PEOPLE','RANGE_MIN','DAYS_BEFORE_EVENT','DAY_OF_WEEK') NOT NULL, " +
                "condition_value DECIMAL(10,2) NOT NULL, " +
                "discount_type ENUM('PERCENT','FIXED_AMOUNT') NOT NULL, " +
                "discount_value DECIMAL(10,2) NOT NULL, " +
                "start_date DATE, end_date DATE, " +
                "is_active BOOLEAN DEFAULT TRUE, description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            createTableIfNotExists(conn, "tax_rates",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, rate DECIMAL(5,2) NOT NULL, " +
                "type ENUM('PERCENTAGE','FIXED_AMOUNT') DEFAULT 'PERCENTAGE', " +
                "applicability ENUM('ALL','VENUE','ACTIVITY','SERVICE') DEFAULT 'ALL', " +
                "event_type VARCHAR(50), start_date DATE, end_date DATE, " +
                "is_active BOOLEAN DEFAULT TRUE, description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            createTableIfNotExists(conn, "service_fee_rates",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, rate DECIMAL(5,2) NOT NULL, " +
                "type ENUM('PERCENTAGE','FIXED_AMOUNT') DEFAULT 'PERCENTAGE', " +
                "applicability ENUM('ALL','VENUE_ONLY','ACTIVITY_ONLY') DEFAULT 'ALL', " +
                "event_type VARCHAR(50), " +
                "min_amount DECIMAL(10,2), max_amount DECIMAL(10,2), " +
                "start_date DATE, end_date DATE, " +
                "is_active BOOLEAN DEFAULT TRUE, description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            try {
                createTableIfNotExists(conn, "payment_schedules",
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "booking_id BIGINT NOT NULL, installment_number INT NOT NULL, " +
                    "amount DECIMAL(10,2) NOT NULL, due_date DATE NOT NULL, paid_date DATE, " +
                    "status ENUM('PENDING','PAID','OVERDUE','CANCELLED') DEFAULT 'PENDING', " +
                    "payment_method VARCHAR(100), description TEXT, " +
                    "is_deposit BOOLEAN DEFAULT FALSE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, reminder_sent TIMESTAMP, " +
                    "FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE");
            } catch (Exception e) {
                System.err.println("payment_schedules FK failed, retrying without: " + e.getMessage());
                createTableIfNotExists(conn, "payment_schedules",
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "booking_id BIGINT NOT NULL, installment_number INT NOT NULL, " +
                    "amount DECIMAL(10,2) NOT NULL, due_date DATE NOT NULL, paid_date DATE, " +
                    "status ENUM('PENDING','PAID','OVERDUE','CANCELLED') DEFAULT 'PENDING', " +
                    "payment_method VARCHAR(100), description TEXT, " +
                    "is_deposit BOOLEAN DEFAULT FALSE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, reminder_sent TIMESTAMP");
            }

            addColumnIfNotExists(conn, "venues", "base_fee", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "venues", "per_person_fee", "DECIMAL(10,2)");
            addColumnIfNotExists(conn, "venues", "capacity", "INT");
            addColumnIfNotExists(conn, "venues", "has_pricing_rules", "BOOLEAN DEFAULT FALSE");

            seedPricingData(conn);
            System.out.println("Pricing schema migrations completed.");
        } catch (Exception e) {
            System.err.println("Error running pricing migrations: " + e.getMessage());
        }
    }

    private static void seedPricingData(Connection conn) {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM tax_rates");
            if (rs.next() && rs.getInt("cnt") == 0) {
                conn.createStatement().execute("INSERT INTO tax_rates (name, rate, type, applicability, description) VALUES " +
                    "('VAT', 19.0, 'PERCENTAGE', 'ALL', 'Standard VAT rate'), " +
                    "('Service Tax', 5.0, 'PERCENTAGE', 'SERVICE', 'Service tax on additional services'), " +
                    "('Event Tax', 2.0, 'PERCENTAGE', 'ACTIVITY', 'Tax on activities')");
                System.out.println("Seeded default tax_rates.");
            }
        } catch (Exception e) { System.err.println("seed tax_rates: " + e.getMessage()); }

        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM service_fee_rates");
            if (rs.next() && rs.getInt("cnt") == 0) {
                conn.createStatement().execute("INSERT INTO service_fee_rates (name, rate, type, applicability, description) VALUES " +
                    "('Processing Fee', 2.5, 'PERCENTAGE', 'ALL', 'Standard processing fee'), " +
                    "('Platform Fee', 1.0, 'PERCENTAGE', 'ALL', 'Platform usage fee'), " +
                    "('Payment Processing', 0.5, 'PERCENTAGE', 'ALL', 'Payment gateway fee')");
                System.out.println("Seeded default service_fee_rates.");
            }
        } catch (Exception e) { System.err.println("seed service_fee_rates: " + e.getMessage()); }

        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM pricing_rules");
            if (rs.next() && rs.getInt("cnt") == 0) {
                conn.createStatement().execute("INSERT INTO pricing_rules (event_type, rule_type, condition_type, condition_value, discount_type, discount_value, description) VALUES " +
                    "('TeamBuilding','GROUP_SIZE','MIN_PEOPLE',10,'PERCENT',10.0,'10% off for groups of 10+'), " +
                    "('TeamBuilding','GROUP_SIZE','MIN_PEOPLE',25,'PERCENT',15.0,'15% off for groups of 25+'), " +
                    "('TeamBuilding','EARLY_BIRD','DAYS_BEFORE_EVENT',30,'PERCENT',20.0,'20% off booking 30+ days early'), " +
                    "('TeamBuilding','EARLY_BIRD','DAYS_BEFORE_EVENT',14,'PERCENT',10.0,'10% off booking 14+ days early'), " +
                    "(NULL,'OFF_PEAK','DAY_OF_WEEK',2,'PERCENT',5.0,'5% off Monday'), " +
                    "(NULL,'OFF_PEAK','DAY_OF_WEEK',3,'PERCENT',5.0,'5% off Tuesday'), " +
                    "(NULL,'OFF_PEAK','DAY_OF_WEEK',4,'PERCENT',5.0,'5% off Wednesday')");
                System.out.println("Seeded default pricing_rules.");
            }
        } catch (Exception e) { System.err.println("seed pricing_rules: " + e.getMessage()); }

        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) as cnt FROM coupons");
            if (rs.next() && rs.getInt("cnt") == 0) {
                conn.createStatement().execute("INSERT INTO coupons (code, type, value, start_date, end_date, usage_limit, min_spend, description) VALUES " +
                    "('WELCOME10','PERCENT',10.0,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 30 DAY),100,50.0,'Welcome discount'), " +
                    "('TEAM2024','FIXED',25.0,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 90 DAY),50,100.0,'Team building special offer'), " +
                    "('EARLYBIRD','PERCENT',15.0,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 60 DAY),200,200.0,'Early bird discount'), " +
                    "('CORPORATE20','PERCENT',20.0,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 180 DAY),100,500.0,'Corporate discount')");
                System.out.println("Seeded sample coupons.");
            }
        } catch (Exception e) { System.err.println("seed coupons: " + e.getMessage()); }
    }

    /**
     * Helper method to add a column to a table if it doesn't exist.
     */
    private static void addColumnIfNotExists(Connection conn, String tableName, String columnName, String columnType) {
        try {
            // BUG FIX: do NOT use toUpperCase() — MySQL on Linux is case-sensitive
            // and metadata lookups must match the exact table/column name casing.
            ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
            if (rs.next()) {
                System.out.println("Column " + tableName + "." + columnName + " already exists, skipping.");
            } else {
                Statement stmt = conn.createStatement();
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
                stmt.close();
                System.out.println("Added column " + tableName + "." + columnName);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error checking/adding column " + tableName + "." + columnName + ": " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create a table if it doesn't exist.
     */
    private static void createTableIfNotExists(Connection conn, String tableName, String definition) {
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"});
            if (rs.next()) {
                System.out.println("Table " + tableName + " already exists, skipping.");
            } else {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE " + tableName + " (" + definition + ")");
                stmt.close();
                System.out.println("Created table " + tableName);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
        }
    }
}