package com.synapseevent.service;

import com.synapseevent.entities.UserPreferences;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class UserPreferencesService implements IService<UserPreferences> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(UserPreferences prefs) throws SQLException {
        String sql = "INSERT INTO UserPreferences (user_id, preferred_categories, preferred_locations, max_price, min_rating) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, prefs.getUserId());
            stmt.setString(2, prefs.getPreferredCategories());
            stmt.setString(3, prefs.getPreferredLocations());
            stmt.setDouble(4, prefs.getMaxPrice() != null ? prefs.getMaxPrice() : 0.0);
            stmt.setInt(5, prefs.getMinRating() != null ? prefs.getMinRating() : 0);
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                prefs.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<UserPreferences> readAll() throws SQLException {
        List<UserPreferences> prefs = new ArrayList<>();
        String sql = "SELECT * FROM UserPreferences";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UserPreferences pref = new UserPreferences(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("preferred_categories"),
                    rs.getString("preferred_locations"),
                    rs.getDouble("max_price"),
                    rs.getInt("min_rating")
                );
                prefs.add(pref);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return prefs;
    }

    @Override
    public UserPreferences findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM UserPreferences WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserPreferences(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("preferred_categories"),
                    rs.getString("preferred_locations"),
                    rs.getDouble("max_price"),
                    rs.getInt("min_rating")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(UserPreferences prefs) throws SQLException {
        String sql = "UPDATE UserPreferences SET preferred_categories = ?, preferred_locations = ?, max_price = ?, min_rating = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefs.getPreferredCategories());
            stmt.setString(2, prefs.getPreferredLocations());
            stmt.setDouble(3, prefs.getMaxPrice() != null ? prefs.getMaxPrice() : 0.0);
            stmt.setInt(4, prefs.getMinRating() != null ? prefs.getMinRating() : 0);
            stmt.setLong(5, prefs.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(UserPreferences prefs) throws SQLException {
        if (prefs.getId() != null) {
            String sql = "DELETE FROM UserPreferences WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, prefs.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public UserPreferences findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM UserPreferences WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserPreferences(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("preferred_categories"),
                    rs.getString("preferred_locations"),
                    rs.getDouble("max_price"),
                    rs.getInt("min_rating")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
}