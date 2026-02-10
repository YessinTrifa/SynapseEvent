package com.synapseevent.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        Connection conn = MaConnection.getInstance().getConnection();
        try {
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
            e.printStackTrace();
        }
    }
}
