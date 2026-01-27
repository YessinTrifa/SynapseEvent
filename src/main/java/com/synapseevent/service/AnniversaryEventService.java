package com.synapseevent.service;

import com.synapseevent.entities.AnniversaryEvent;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class AnniversaryEventService implements IService<AnniversaryEvent> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(AnniversaryEvent event) throws SQLException {
        String sql = "INSERT INTO AnniversaryEvent (name, date, start_time, end_time, location, capacity, price, organizer, category, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());
            stmt.setInt(6, event.getCapacity() != null ? event.getCapacity() : 0);
            stmt.setDouble(7, event.getPrice() != null ? event.getPrice() : 0.0);
            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getCategory());
            stmt.setString(10, event.getDescription());
            stmt.setString(11, event.getStatus());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<AnniversaryEvent> readAll() throws SQLException {
        List<AnniversaryEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM AnniversaryEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AnniversaryEvent event = new AnniversaryEvent(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price"),
                    rs.getString("organizer"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    @Override
    public AnniversaryEvent findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM AnniversaryEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AnniversaryEvent(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price"),
                    rs.getString("organizer"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(AnniversaryEvent event) throws SQLException {
        String sql = "UPDATE AnniversaryEvent SET name = ?, date = ?, start_time = ?, end_time = ?, location = ?, capacity = ?, price = ?, organizer = ?, category = ?, description = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setTime(3, event.getStartTime() != null ? Time.valueOf(event.getStartTime()) : null);
            stmt.setTime(4, event.getEndTime() != null ? Time.valueOf(event.getEndTime()) : null);
            stmt.setString(5, event.getLocation());
            stmt.setInt(6, event.getCapacity() != null ? event.getCapacity() : 0);
            stmt.setDouble(7, event.getPrice() != null ? event.getPrice() : 0.0);
            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getCategory());
            stmt.setString(10, event.getDescription());
            stmt.setString(11, event.getStatus());
            stmt.setLong(12, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(AnniversaryEvent event) throws SQLException {
        if (event.getId() != null) {
            String sql = "DELETE FROM AnniversaryEvent WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, event.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<AnniversaryEvent> getPublishedEvents() throws SQLException {
        List<AnniversaryEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM AnniversaryEvent WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AnniversaryEvent event = new AnniversaryEvent(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price"),
                    rs.getString("organizer"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<AnniversaryEvent> searchEvents(String keyword, String category, String location, Double maxPrice) throws SQLException {
        List<AnniversaryEvent> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM AnniversaryEvent WHERE status = 'published'");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location + "%");
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AnniversaryEvent event = new AnniversaryEvent(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("start_time") != null ? rs.getTime("start_time").toLocalTime() : null,
                    rs.getTime("end_time") != null ? rs.getTime("end_time").toLocalTime() : null,
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getDouble("price"),
                    rs.getString("organizer"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public String generateICal(AnniversaryEvent event) {
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\n");
        ical.append("VERSION:2.0\n");
        ical.append("PRODID:-//SynapseEvent//Calendar//EN\n");
        ical.append("BEGIN:VEVENT\n");
        ical.append("UID:").append(event.getId()).append("@synapseevent.com\n");
        ical.append("DTSTART:").append(event.getDate().toString().replace("-", "")).append("T");
        if (event.getStartTime() != null) {
            ical.append(event.getStartTime().toString().replace(":", "")).append("00\n");
        } else {
            ical.append("000000\n");
        }
        ical.append("DTEND:").append(event.getDate().toString().replace("-", "")).append("T");
        if (event.getEndTime() != null) {
            ical.append(event.getEndTime().toString().replace(":", "")).append("00\n");
        } else {
            ical.append("235959\n");
        }
        ical.append("SUMMARY:").append(event.getName()).append("\n");
        if (event.getDescription() != null) {
            ical.append("DESCRIPTION:").append(event.getDescription().replace("\n", "\\n")).append("\n");
        }
        if (event.getLocation() != null) {
            ical.append("LOCATION:").append(event.getLocation()).append("\n");
        }
        ical.append("END:VEVENT\n");
        ical.append("END:VCALENDAR\n");
        return ical.toString();
    }
}