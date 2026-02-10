package com.synapseevent.service;

import com.synapseevent.entities.PartyingEvent;
import com.synapseevent.entities.Venue;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class PartyingEventService implements IService<PartyingEvent> {
    private Connection conn = MaConnection.getInstance().getConnection();
    private VenueService venueService = new VenueService();

    @Override
    public boolean ajouter(PartyingEvent event) throws SQLException {
        String sql = "INSERT INTO PartyingEvent (name, date, start_time, end_time, venue_id, capacity, price, organizer, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            if (event.getStartTime() != null) {
                stmt.setTime(3, Time.valueOf(event.getStartTime()));
            } else {
                stmt.setNull(3, Types.TIME);
            }
            if (event.getEndTime() != null) {
                stmt.setTime(4, Time.valueOf(event.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIME);
            }
            if (event.getVenueId() != null) {
                stmt.setLong(5, event.getVenueId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            if (event.getCapacity() != null) {
                stmt.setInt(6, event.getCapacity());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (event.getPrice() != null) {
                stmt.setDouble(7, event.getPrice());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getDescription());
            stmt.setString(10, event.getStatus());
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
    public List<PartyingEvent> readAll() throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    @Override
    public PartyingEvent findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM PartyingEvent WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(PartyingEvent event) throws SQLException {
        String sql = "UPDATE PartyingEvent SET name = ?, date = ?, start_time = ?, end_time = ?, venue_id = ?, capacity = ?, price = ?, organizer = ?, description = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            if (event.getStartTime() != null) {
                stmt.setTime(3, Time.valueOf(event.getStartTime()));
            } else {
                stmt.setNull(3, Types.TIME);
            }
            if (event.getEndTime() != null) {
                stmt.setTime(4, Time.valueOf(event.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIME);
            }
            if (event.getVenueId() != null) {
                stmt.setLong(5, event.getVenueId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            if (event.getCapacity() != null) {
                stmt.setInt(6, event.getCapacity());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (event.getPrice() != null) {
                stmt.setDouble(7, event.getPrice());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setString(8, event.getOrganizer());
            stmt.setString(9, event.getDescription());
            stmt.setString(10, event.getStatus());
            stmt.setLong(11, event.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(PartyingEvent event) throws SQLException {
        if (event.getId() != null) {
            String sql = "DELETE FROM PartyingEvent WHERE id = ?";
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

    public List<PartyingEvent> getPublishedEvents() throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent WHERE status = 'published'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PartyingEvent> getEventsByVenue(Long venueId) throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent WHERE venue_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, venueId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PartyingEvent> getEventsByDate(LocalDate date) throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent WHERE date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PartyingEvent> getPublishedEventsByDate(LocalDate date) throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent WHERE status = 'published' AND date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    public List<PartyingEvent> getPublishedEventsByVenue(Long venueId) throws SQLException {
        List<PartyingEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM PartyingEvent WHERE status = 'published' AND venue_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, venueId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PartyingEvent event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return events;
    }

    private PartyingEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        PartyingEvent event = new PartyingEvent();
        event.setId(rs.getLong("id"));
        event.setName(rs.getString("name"));
        event.setDate(rs.getDate("date").toLocalDate());
        
        Time startTime = rs.getTime("start_time");
        if (startTime != null) {
            event.setStartTime(startTime.toLocalTime());
        }
        
        Time endTime = rs.getTime("end_time");
        if (endTime != null) {
            event.setEndTime(endTime.toLocalTime());
        }
        
        long venueId = rs.getLong("venue_id");
        if (!rs.wasNull()) {
            event.setVenueId(venueId);
            try {
                Venue venue = venueService.findbyId(venueId);
                event.setVenue(venue);
            } catch (SQLException e) {
                // Ignore - venue might not exist
            }
        }
        
        int capacity = rs.getInt("capacity");
        if (!rs.wasNull()) {
            event.setCapacity(capacity);
        }
        
        double price = rs.getDouble("price");
        if (!rs.wasNull()) {
            event.setPrice(price);
        }
        
        event.setOrganizer(rs.getString("organizer"));
        event.setDescription(rs.getString("description"));
        event.setStatus(rs.getString("status"));
        
        return event;
    }
}
