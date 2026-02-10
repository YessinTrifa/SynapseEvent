package com.synapseevent.service;

import com.synapseevent.entities.Venue;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueService implements IService<Venue> {
    private Connection conn = MaConnection.getInstance().getConnection();

    @Override
    public boolean ajouter(Venue venue) throws SQLException {
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
}
