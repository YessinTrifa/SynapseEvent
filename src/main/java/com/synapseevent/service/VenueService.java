package com.synapseevent.service;

import com.synapseevent.entities.Venue;
import com.synapseevent.entities.Court;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VenueService implements IService<Venue> {
    private final MaConnection db = MaConnection.getInstance();

    // Court-related methods
    public List<Court> getAllCourts() {
        List<Court> courts = new ArrayList<>();
        String sql = """
            SELECT c.id, c.name, c.venue_id, v.name as venue_name, v.address, v.city,
                   c.is_indoor, c.price_per_hour, c.available, c.description, c.amenities
            FROM Court c
            JOIN Venue v ON c.venue_id = v.id
            WHERE c.available = true
            ORDER BY c.name
            """;

        try (Connection cnx = MaConnection.getInstance().getConnection();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Court court = new Court();
                court.setId(rs.getLong("id"));
                court.setName(rs.getString("name"));
                court.setVenueId(rs.getLong("venue_id"));
                court.setVenueName(rs.getString("venue_name"));
                court.setAddress(rs.getString("address"));
                court.setCity(rs.getString("city"));
                court.setIndoor(rs.getBoolean("is_indoor"));
                court.setPricePerHour(rs.getDouble("price_per_hour"));
                court.setAvailable(rs.getBoolean("available"));
                court.setDescription(rs.getString("description"));
                court.setAmenities(rs.getString("amenities"));
                courts.add(court);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courts: " + e.getMessage());
        }
        return courts;
    }

    public List<Court> getPaddleCourts() {
        List<Court> courts = new ArrayList<>();
        String sql = """
            SELECT c.id, c.name, c.venue_id, v.name as venue_name, v.address, v.city,
                   c.is_indoor, c.price_per_hour, c.available, c.description, c.amenities
            FROM Court c
            JOIN Venue v ON c.venue_id = v.id
            WHERE c.available = true
            ORDER BY c.name
            """;

        try (Connection cnx = MaConnection.getInstance().getConnection();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Court court = new Court();
                court.setId(rs.getLong("id"));
                court.setName(rs.getString("name"));
                court.setVenueId(rs.getLong("venue_id"));
                court.setVenueName(rs.getString("venue_name"));
                court.setAddress(rs.getString("address"));
                court.setCity(rs.getString("city"));
                court.setIndoor(rs.getBoolean("is_indoor"));
                court.setPricePerHour(rs.getDouble("price_per_hour"));
                court.setAvailable(rs.getBoolean("available"));
                court.setDescription(rs.getString("description"));
                court.setAmenities(rs.getString("amenities"));
                courts.add(court);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paddle courts: " + e.getMessage());
        }
        return courts;
    }

    public Court getCourtById(Long courtId) {
        String sql = """
            SELECT c.id, c.name, c.venue_id, v.name as venue_name, v.address, v.city,
                   c.is_indoor, c.price_per_hour, c.available, c.description, c.amenities
            FROM Court c
            JOIN Venue v ON c.venue_id = v.id
            WHERE c.id = ?
            """;

        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setLong(1, courtId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Court court = new Court();
                court.setId(rs.getLong("id"));
                court.setName(rs.getString("name"));
                court.setVenueId(rs.getLong("venue_id"));
                court.setVenueName(rs.getString("venue_name"));
                court.setAddress(rs.getString("address"));
                court.setCity(rs.getString("city"));
                court.setIndoor(rs.getBoolean("is_indoor"));
                court.setPricePerHour(rs.getDouble("price_per_hour"));
                court.setAvailable(rs.getBoolean("available"));
                court.setDescription(rs.getString("description"));
                court.setAmenities(rs.getString("amenities"));
                return court;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching court by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean isCourtAvailable(Long courtId, Date date, Time startTime, Time endTime) {
        String sql = """
            SELECT COUNT(*) as conflict_count
            FROM court_reservations
            WHERE court_id = ?
              AND reservation_date = ?
              AND status = 'CONFIRMED'
              AND NOT (end_time <= ? OR start_time >= ?)
            """;

        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setLong(1, courtId);
            stmt.setDate(2, date);
            stmt.setTime(3, startTime);
            stmt.setTime(4, endTime);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("conflict_count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking court availability: " + e.getMessage());
        }
        return false;
    }

    public boolean createCourtReservation(Long courtId, Long userId, Date date, 
                                          Time startTime, Time endTime, Double totalPrice) {
        String sql = """
            INSERT INTO court_reservations (court_id, user_id, reservation_date, 
                                          start_time, end_time, total_price, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED', NOW())
            """;

        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, courtId);
            stmt.setLong(2, userId);
            stmt.setDate(3, date);
            stmt.setTime(4, startTime);
            stmt.setTime(5, endTime);
            stmt.setDouble(6, totalPrice);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error creating court reservation: " + e.getMessage());
            return false;
        }
    }
    
    // Generic reservation method for any event
    public boolean reserveEvent(int eventId, int userId, int seats, String eventType) {
        Connection cnx = null;
        try {
            cnx = MaConnection.getInstance().getConnection();
            cnx.setAutoCommit(false);
            
            // Check availability based on event type
            String checkSql = "";
            if ("PADDLE".equals(eventType)) {
                checkSql = "SELECT p.capacity - COALESCE(SUM(r.seats), 0) as available " +
                    "FROM PaddleEvent p " +
                    "LEFT JOIN reservations r ON p.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'PADDLE' " +
                    "WHERE p.id = ? AND p.status = 'published' " +
                    "GROUP BY p.capacity";
            } else if ("FORMATION".equals(eventType)) {
                checkSql = "SELECT f.capacity - COALESCE(SUM(r.seats), 0) as available " +
                    "FROM FormationEvent f " +
                    "LEFT JOIN reservations r ON f.id = r.event_id AND r.status = 'CONFIRMED' AND r.event_type = 'FORMATION' " +
                    "WHERE f.id = ? AND f.status = 'published' " +
                    "GROUP BY f.capacity";
            }
            
            if (checkSql.isEmpty()) {
                return false;
            }
            
            try (PreparedStatement checkStmt = cnx.prepareStatement(checkSql)) {
                checkStmt.setInt(1, eventId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int available = rs.getInt("available");
                    if (available < seats) {
                        cnx.rollback();
                        return false;
                    }
                } else {
                    cnx.rollback();
                    return false;
                }
            }
            
            // Insert reservation
            String insertSql = "INSERT INTO reservations (event_id, user_id, seats, status, event_type, created_at) " +
                "VALUES (?, ?, ?, 'CONFIRMED', ?, NOW())";
            
            try (PreparedStatement insertStmt = cnx.prepareStatement(insertSql)) {
                insertStmt.setInt(1, eventId);
                insertStmt.setInt(2, userId);
                insertStmt.setInt(3, seats);
                insertStmt.setString(4, eventType);
                
                int rowsAffected = insertStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    cnx.commit();
                    return true;
                } else {
                    cnx.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during reservation: " + e.getMessage());
            try {
                if (cnx != null) cnx.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (cnx != null) cnx.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean ajouter(Venue venue) throws SQLException {
        // TODO: Implement
        return false;
    }

    @Override
    public List<Venue> readAll() throws SQLException {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venue";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setCity(rs.getString("city"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getObject("rating") != null ? rs.getDouble("rating") : null);
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                venues.add(venue);
            }
        }
        
        return venues;
    }

    @Override
    public Venue findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM Venue WHERE id = ?";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setAddress(rs.getString("address"));
                venue.setCity(rs.getString("city"));
                venue.setPhone(rs.getString("phone"));
                venue.setEmail(rs.getString("email"));
                venue.setDescription(rs.getString("description"));
                venue.setImageUrl(rs.getString("image_url"));
                return venue;
            }
        }
        
        return null;
    }

    @Override
    public boolean modifier(Venue venue) throws SQLException {
        // TODO: Implement
        return false;
    }

    @Override
    public boolean supprimer(Venue venue) throws SQLException {
        // TODO: Implement
        return false;
    }
    
    // Additional methods needed by controllers
    public List<String> getAllCities() {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT city FROM Venue ORDER BY city";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String city = rs.getString("city");
                if (city != null && !city.isBlank()) {
                    cities.add(city);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cities: " + e.getMessage());
        }
        return cities;
    }
    
    public List<Venue> findByTypeAndCity(String type, String city) {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venue WHERE 1=1";
        
        if (type != null && !type.isBlank() && !"All Types".equals(type)) {
            sql += " AND type = '" + type + "'";
        }
        if (city != null && !city.isBlank() && !"All Cities".equals(city)) {
            sql += " AND address LIKE '%" + city + "%'";
        }
        sql += " ORDER BY name";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setCity(rs.getString("city"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getObject("rating") != null ? rs.getDouble("rating") : null);
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                venues.add(venue);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching venues: " + e.getMessage());
        }
        return venues;
    }
    
    public List<Venue> findByType(String type) {
        return findByTypeAndCity(type, null);
    }
    
    /**
     * Get available time slots for a court on a specific date
     * Returns list of available hours (e.g., ["07:00", "08:00", "09:00"])
     */
    public List<String> getAvailableTimeSlots(Long courtId, java.sql.Date date) {
        List<String> allSlots = new ArrayList<>();
        // Generate all possible time slots from 7:00 to 22:00
        for (int hour = 7; hour <= 22; hour++) {
            allSlots.add(String.format("%02d:00", hour));
        }
        
        List<String> bookedSlots = new ArrayList<>();
        String sql = "SELECT start_time, end_time FROM court_reservation " +
                     "WHERE court_id = ? AND reservation_date = ? AND status != 'cancelled'";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setLong(1, courtId);
            stmt.setDate(2, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Time startTime = rs.getTime("start_time");
                    Time endTime = rs.getTime("end_time");
                    
                    if (startTime != null && endTime != null) {
                        int startHour = startTime.toLocalTime().getHour();
                        int endHour = endTime.toLocalTime().getHour();
                        
                        // Mark all hours in the booked range as unavailable
                        for (int h = startHour; h < endHour; h++) {
                            bookedSlots.add(String.format("%02d:00", h));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available slots: " + e.getMessage());
        }
        
        // Return only available slots
        List<String> availableSlots = new ArrayList<>();
        for (String slot : allSlots) {
            if (!bookedSlots.contains(slot)) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }
    
    /**
     * Get available time slots for a court on a specific date with 1h30 increments
     * Returns list of available time slots (e.g., ["07:00", "08:30", "10:00", ...])
     */
    public List<String> getAvailableTimeSlots1h30(Long courtId, java.sql.Date date) {
        List<String> allSlots = new ArrayList<>();
        // Generate all possible time slots from 7:00 to 22:00 with 1h30 increments
        for (int hour = 7; hour <= 22; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                // Skip 22:30 as it would go past closing time
                if (hour == 22 && minute > 0) break;
                allSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        
        // Get booked slots
        List<String> bookedSlots = new ArrayList<>();
        String sql = "SELECT start_time, end_time FROM court_reservation " +
                     "WHERE court_id = ? AND reservation_date = ? AND status != 'cancelled'";
        
        try (Connection cnx = MaConnection.getInstance().getConnection();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setLong(1, courtId);
            stmt.setDate(2, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Time startTime = rs.getTime("start_time");
                    Time endTime = rs.getTime("end_time");
                    
                    if (startTime != null && endTime != null) {
                        // Mark all 30-minute slots in the booked range as unavailable
                        LocalTime start = startTime.toLocalTime();
                        LocalTime end = endTime.toLocalTime();
                        
                        LocalTime current = start;
                        while (current.isBefore(end)) {
                            bookedSlots.add(String.format("%02d:%02d", current.getHour(), current.getMinute()));
                            current = current.plusMinutes(30);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available slots: " + e.getMessage());
        }
        
        // Return only available slots
        List<String> availableSlots = new ArrayList<>();
        for (String slot : allSlots) {
            if (!bookedSlots.contains(slot)) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }
}
