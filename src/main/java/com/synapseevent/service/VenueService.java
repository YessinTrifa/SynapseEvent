package com.synapseevent.service;

import com.synapseevent.entities.Venue;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueService implements IService<Venue> {
    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(Venue venue) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO Venue (name, type, address, contact_info, price_range, rating, description, amenities) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getType());
            stmt.setString(3, venue.getAddress());
            stmt.setString(4, venue.getContactInfo());
            stmt.setString(5, venue.getPriceRange());
            stmt.setDouble(6, venue.getRating() != null ? venue.getRating() : 0);
            stmt.setString(7, venue.getDescription());
            stmt.setString(8, venue.getAmenities());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                venue.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Venue> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venue";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getDouble("rating"));
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                venues.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return venues;
    }

    @Override
    public Venue findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM Venue WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getDouble("rating"));
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                return venue;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(Venue venue) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE Venue SET name = ?, type = ?, address = ?, contact_info = ?, price_range = ?, rating = ?, description = ?, amenities = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, venue.getName());
            stmt.setString(2, venue.getType());
            stmt.setString(3, venue.getAddress());
            stmt.setString(4, venue.getContactInfo());
            stmt.setString(5, venue.getPriceRange());
            stmt.setDouble(6, venue.getRating() != null ? venue.getRating() : 0);
            stmt.setString(7, venue.getDescription());
            stmt.setString(8, venue.getAmenities());
            stmt.setLong(9, venue.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(Venue venue) throws SQLException {
        Connection conn = db.requireConnection();
        if (venue.getId() != null) {
            String sql = "DELETE FROM Venue WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, venue.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<Venue> findByType(String type) throws SQLException {
        Connection conn = db.requireConnection();
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM Venue WHERE type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getDouble("rating"));
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                venues.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return venues;
    }

    public List<String> getAllTypes() throws SQLException {
        Connection conn = db.requireConnection();
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM Venue ORDER BY type";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return types;
    }

    public List<String> getAllCities() throws SQLException {
        Connection conn = db.requireConnection();
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT address FROM Venue WHERE address IS NOT NULL ORDER BY address";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String address = rs.getString("address");
                // Extract city from address (assume last part is city)
                if (address != null && !address.isEmpty()) {
                    String[] parts = address.split(",");
                    String city = parts[parts.length - 1].trim();
                    if (!city.isEmpty() && !cities.contains(city)) {
                        cities.add(city);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return cities;
    }

    public List<Venue> findByTypeAndCity(String type, String city) throws SQLException {
        Connection conn = db.requireConnection();
        List<Venue> venues = new ArrayList<>();
        String sql;
        if (city != null && !city.isEmpty() && !"All Cities".equals(city)) {
            if (type != null && !type.isEmpty() && !"All Types".equals(type)) {
                sql = "SELECT * FROM Venue WHERE type = ? AND address LIKE ?";
            } else {
                sql = "SELECT * FROM Venue WHERE address LIKE ?";
            }
        } else {
            if (type != null && !type.isEmpty() && !"All Types".equals(type)) {
                sql = "SELECT * FROM Venue WHERE type = ?";
            } else {
                sql = "SELECT * FROM Venue";
            }
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (type != null && !type.isEmpty() && !"All Types".equals(type)) {
                stmt.setString(paramIndex++, type);
            }
            if (city != null && !city.isEmpty() && !"All Cities".equals(city)) {
                stmt.setString(paramIndex, "%" + city + "%");
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setId(rs.getLong("id"));
                venue.setName(rs.getString("name"));
                venue.setType(rs.getString("type"));
                venue.setAddress(rs.getString("address"));
                venue.setContactInfo(rs.getString("contact_info"));
                venue.setPriceRange(rs.getString("price_range"));
                venue.setRating(rs.getDouble("rating"));
                venue.setDescription(rs.getString("description"));
                venue.setAmenities(rs.getString("amenities"));
                venues.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return venues;
    }
}


