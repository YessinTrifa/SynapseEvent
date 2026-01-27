package com.synapseevent.service;

import com.synapseevent.entities.EventTemplate;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class EventTemplateService implements IService<EventTemplate> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(EventTemplate template) throws SQLException {
        String sql = "INSERT INTO EventTemplate (name, event_type, default_start_time, default_end_time, default_capacity, default_price, default_category, default_description, template_description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, template.getName());
            stmt.setString(2, template.getEventType());
            stmt.setTime(3, template.getDefaultStartTime() != null ? Time.valueOf(template.getDefaultStartTime()) : null);
            stmt.setTime(4, template.getDefaultEndTime() != null ? Time.valueOf(template.getDefaultEndTime()) : null);
            stmt.setInt(5, template.getDefaultCapacity() != null ? template.getDefaultCapacity() : 0);
            stmt.setDouble(6, template.getDefaultPrice() != null ? template.getDefaultPrice() : 0.0);
            stmt.setString(7, template.getDefaultCategory());
            stmt.setString(8, template.getDefaultDescription());
            stmt.setString(9, template.getTemplateDescription());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                template.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<EventTemplate> readAll() throws SQLException {
        List<EventTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM EventTemplate";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EventTemplate template = new EventTemplate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("event_type"),
                    rs.getTime("default_start_time") != null ? rs.getTime("default_start_time").toLocalTime() : null,
                    rs.getTime("default_end_time") != null ? rs.getTime("default_end_time").toLocalTime() : null,
                    rs.getInt("default_capacity"),
                    rs.getDouble("default_price"),
                    rs.getString("default_category"),
                    rs.getString("default_description"),
                    rs.getString("template_description")
                );
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return templates;
    }

    @Override
    public EventTemplate findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM EventTemplate WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new EventTemplate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("event_type"),
                    rs.getTime("default_start_time") != null ? rs.getTime("default_start_time").toLocalTime() : null,
                    rs.getTime("default_end_time") != null ? rs.getTime("default_end_time").toLocalTime() : null,
                    rs.getInt("default_capacity"),
                    rs.getDouble("default_price"),
                    rs.getString("default_category"),
                    rs.getString("default_description"),
                    rs.getString("template_description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(EventTemplate template) throws SQLException {
        String sql = "UPDATE EventTemplate SET name = ?, event_type = ?, default_start_time = ?, default_end_time = ?, default_capacity = ?, default_price = ?, default_category = ?, default_description = ?, template_description = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, template.getName());
            stmt.setString(2, template.getEventType());
            stmt.setTime(3, template.getDefaultStartTime() != null ? Time.valueOf(template.getDefaultStartTime()) : null);
            stmt.setTime(4, template.getDefaultEndTime() != null ? Time.valueOf(template.getDefaultEndTime()) : null);
            stmt.setInt(5, template.getDefaultCapacity() != null ? template.getDefaultCapacity() : 0);
            stmt.setDouble(6, template.getDefaultPrice() != null ? template.getDefaultPrice() : 0.0);
            stmt.setString(7, template.getDefaultCategory());
            stmt.setString(8, template.getDefaultDescription());
            stmt.setString(9, template.getTemplateDescription());
            stmt.setLong(10, template.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(EventTemplate template) throws SQLException {
        if (template.getId() != null) {
            String sql = "DELETE FROM EventTemplate WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, template.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<EventTemplate> getTemplatesByType(String eventType) throws SQLException {
        List<EventTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM EventTemplate WHERE event_type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EventTemplate template = new EventTemplate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("event_type"),
                    rs.getTime("default_start_time") != null ? rs.getTime("default_start_time").toLocalTime() : null,
                    rs.getTime("default_end_time") != null ? rs.getTime("default_end_time").toLocalTime() : null,
                    rs.getInt("default_capacity"),
                    rs.getDouble("default_price"),
                    rs.getString("default_category"),
                    rs.getString("default_description"),
                    rs.getString("template_description")
                );
                templates.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return templates;
    }
}