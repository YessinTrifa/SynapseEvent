package com.synapseevent.service;

import com.synapseevent.entities.Review;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class ReviewService implements IService<Review> {
    private final MaConnection db = MaConnection.getInstance();

    @Override
    public boolean ajouter(Review review) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO Review (user_id, event_type, event_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, review.getUserId());
            stmt.setString(2, review.getEventType());
            stmt.setLong(3, review.getEventId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            stmt.setTimestamp(6, Timestamp.valueOf(review.getCreatedAt()));
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                review.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Review> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Review";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Review review = new Review(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("event_type"),
                    rs.getLong("event_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return reviews;
    }

    @Override
    public Review findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM Review WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Review(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("event_type"),
                    rs.getLong("event_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(Review review) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE Review SET rating = ?, comment = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setLong(3, review.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(Review review) throws SQLException {
        Connection conn = db.requireConnection();
        if (review.getId() != null) {
            String sql = "DELETE FROM Review WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, review.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<Review> getReviewsByEvent(String eventType, Long eventId) throws SQLException {
        Connection conn = db.requireConnection();
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Review WHERE event_type = ? AND event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType);
            stmt.setLong(2, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("event_type"),
                    rs.getLong("event_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return reviews;
    }

    public double getAverageRating(String eventType, Long eventId) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT AVG(rating) as avg_rating FROM Review WHERE event_type = ? AND event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType);
            stmt.setLong(2, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return 0.0;
    }
}
