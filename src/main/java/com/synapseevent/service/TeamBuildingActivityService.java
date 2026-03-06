package com.synapseevent.service;

import com.synapseevent.entities.TeamBuildingActivity;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamBuildingActivityService implements IService<TeamBuildingActivity> {
    
    @Override
    public List<TeamBuildingActivity> readAll() {
        List<TeamBuildingActivity> activities = new ArrayList<>();
        String query = "SELECT * FROM TeamBuildingActivity WHERE is_active = TRUE ORDER BY category, name";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }
    
    public List<TeamBuildingActivity> readAllIncludingInactive() {
        List<TeamBuildingActivity> activities = new ArrayList<>();
        String query = "SELECT * FROM TeamBuildingActivity ORDER BY category, name";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }
    
    @Override
    public TeamBuildingActivity findbyId(Long id) {
        String query = "SELECT * FROM TeamBuildingActivity WHERE id = ?";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToActivity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean ajouter(TeamBuildingActivity activity) {
        String query = "INSERT INTO TeamBuildingActivity (name, description, category, duration_minutes, " +
                       "price_per_person, min_participants, max_participants, is_active) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, activity.getName());
            pstmt.setString(2, activity.getDescription());
            pstmt.setString(3, activity.getCategory());
            pstmt.setInt(4, activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 60);
            pstmt.setDouble(5, activity.getPricePerPerson() != null ? activity.getPricePerPerson() : 0.0);
            pstmt.setInt(6, activity.getMinParticipants() != null ? activity.getMinParticipants() : 1);
            pstmt.setInt(7, activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 100);
            pstmt.setBoolean(8, activity.getIsActive() != null ? activity.getIsActive() : true);
            
            int result = pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                activity.setId(rs.getLong(1));
            }
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean modifier(TeamBuildingActivity activity) {
        String query = "UPDATE TeamBuildingActivity SET name = ?, description = ?, category = ?, " +
                       "duration_minutes = ?, price_per_person = ?, min_participants = ?, " +
                       "max_participants = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, activity.getName());
            pstmt.setString(2, activity.getDescription());
            pstmt.setString(3, activity.getCategory());
            pstmt.setInt(4, activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 60);
            pstmt.setDouble(5, activity.getPricePerPerson() != null ? activity.getPricePerPerson() : 0.0);
            pstmt.setInt(6, activity.getMinParticipants() != null ? activity.getMinParticipants() : 1);
            pstmt.setInt(7, activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 100);
            pstmt.setBoolean(8, activity.getIsActive() != null ? activity.getIsActive() : true);
            pstmt.setLong(9, activity.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean supprimer(TeamBuildingActivity activity) {
        String query = "DELETE FROM TeamBuildingActivity WHERE id = ?";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, activity.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<TeamBuildingActivity> getActivitiesByCategory(String category) {
        List<TeamBuildingActivity> activities = new ArrayList<>();
        String query = "SELECT * FROM TeamBuildingActivity WHERE category = ? AND is_active = TRUE";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }
    
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM TeamBuildingActivity WHERE is_active = TRUE ORDER BY category";
        
        try (Connection conn = MaConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    private TeamBuildingActivity mapResultSetToActivity(ResultSet rs) throws SQLException {
        TeamBuildingActivity activity = new TeamBuildingActivity();
        activity.setId(rs.getLong("id"));
        activity.setName(rs.getString("name"));
        activity.setDescription(rs.getString("description"));
        activity.setCategory(rs.getString("category"));
        activity.setDurationMinutes(rs.getInt("duration_minutes"));
        activity.setPricePerPerson(rs.getDouble("price_per_person"));
        activity.setMinParticipants(rs.getInt("min_participants"));
        activity.setMaxParticipants(rs.getInt("max_participants"));
        activity.setIsActive(rs.getBoolean("is_active"));
        return activity;
    }
}
