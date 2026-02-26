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

            System.out.println("Migrations completed.");
        } catch (Exception e) {
            System.err.println("Error running migrations: " + e.getMessage());
        }
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
}