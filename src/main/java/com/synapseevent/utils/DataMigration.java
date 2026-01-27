package com.synapseevent.utils;

import java.sql.*;

public class DataMigration {
    public static void main(String[] args) {
        DataMigration migration = new DataMigration();
        try {
            migration.migrate();
            System.out.println("Migration completed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Migration failed.");
        }
    }
    private Connection conn = MaConnection.getInstance().getConnection();

    public void migrate() throws SQLException {
        // Migrate events to flat structure
        migrateAnniversaryEvents();
        migrateFormationEvents();
        migratePaddleEvents();
        migratePartyingEvents();
        migrateTeamBuildingEvents();
    }

    private long getOrCreateVariant(String variantName, String variantDesc, String typeName, String subcategoryName, String categoryName) throws SQLException {
        long categoryId = findCategoryId(categoryName);
        if (categoryId == -1) {
            categoryId = insertCategory(categoryName, "Events related to " + categoryName.toLowerCase());
        }

        long subcategoryId = findSubcategoryId(subcategoryName, categoryId);
        if (subcategoryId == -1) {
            subcategoryId = insertSubcategory(subcategoryName, subcategoryName + " events", categoryId);
        }

        long typeId = findTypeId(typeName, subcategoryId);
        if (typeId == -1) {
            typeId = insertType(typeName, typeName + " type", subcategoryId);
        }

        long variantId = findVariantId(variantName, typeId);
        if (variantId == -1) {
            variantId = insertVariant(variantName, variantDesc, typeId);
        }

        return variantId;
    }

    private long findCategoryId(String name) throws SQLException {
        String sql = "SELECT id FROM event_category WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");
        }
        return -1;
    }

    private long insertCategory(String name, String desc) throws SQLException {
        String sql = "INSERT INTO event_category (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        }
        return -1;
    }

    private long findSubcategoryId(String name, long categoryId) throws SQLException {
        String sql = "SELECT id FROM event_subcategory WHERE name = ? AND category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setLong(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");
        }
        return -1;
    }

    private long insertSubcategory(String name, String desc, long categoryId) throws SQLException {
        String sql = "INSERT INTO event_subcategory (name, description, category_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setLong(3, categoryId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        }
        return -1;
    }

    private long findTypeId(String name, long subcategoryId) throws SQLException {
        String sql = "SELECT id FROM event_type WHERE name = ? AND subcategory_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setLong(2, subcategoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");
        }
        return -1;
    }

    private long insertType(String name, String desc, long subcategoryId) throws SQLException {
        String sql = "INSERT INTO event_type (name, description, subcategory_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setLong(3, subcategoryId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        }
        return -1;
    }

    private long findVariantId(String name, long typeId) throws SQLException {
        String sql = "SELECT id FROM event_variant WHERE name = ? AND type_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setLong(2, typeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("id");
        }
        return -1;
    }

    private long insertVariant(String name, String desc, long typeId) throws SQLException {
        String sql = "INSERT INTO event_variant (name, description, type_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setLong(3, typeId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        }
        return -1;
    }

    private void migrateAnniversaryEvents() throws SQLException {
        String sql = "SELECT * FROM AnniversaryEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String insertSql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rs.getString("name"));
                    insertStmt.setDate(2, rs.getDate("date"));
                    insertStmt.setTime(3, rs.getTime("start_time"));
                    insertStmt.setTime(4, rs.getTime("end_time"));
                    insertStmt.setString(5, rs.getString("location"));
                    insertStmt.setInt(6, rs.getInt("capacity"));
                    insertStmt.setDouble(7, rs.getDouble("price"));
                    insertStmt.setString(8, rs.getString("organizer"));
                    insertStmt.setString(9, rs.getString("description"));
                    insertStmt.setString(10, rs.getString("status"));
                    insertStmt.setString(11, "Anniversary");
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void migrateFormationEvents() throws SQLException {
        String sql = "SELECT * FROM FormationEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String insertSql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rs.getString("name"));
                    insertStmt.setDate(2, rs.getDate("date"));
                    insertStmt.setString(3, rs.getString("description"));
                    insertStmt.setString(4, rs.getString("status"));
                    insertStmt.setString(5, "Formation");
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void migratePaddleEvents() throws SQLException {
        String sql = "SELECT * FROM PaddleEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String insertSql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rs.getString("name"));
                    insertStmt.setDate(2, rs.getDate("date"));
                    insertStmt.setString(3, rs.getString("description"));
                    insertStmt.setString(4, rs.getString("status"));
                    insertStmt.setString(5, "Paddle");
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void migratePartyingEvents() throws SQLException {
        String sql = "SELECT * FROM PartyingEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String insertSql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rs.getString("name"));
                    insertStmt.setDate(2, rs.getDate("date"));
                    insertStmt.setString(3, rs.getString("description"));
                    insertStmt.setString(4, rs.getString("status"));
                    insertStmt.setString(5, "Partying");
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void migrateTeamBuildingEvents() throws SQLException {
        String sql = "SELECT * FROM TeamBuildingEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String insertSql = "INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, NULL, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rs.getString("name"));
                    insertStmt.setDate(2, rs.getDate("date"));
                    insertStmt.setString(3, rs.getString("description"));
                    insertStmt.setString(4, rs.getString("status"));
                    insertStmt.setString(5, "TeamBuilding");
                    insertStmt.executeUpdate();
                }
            }
        }
    }
}