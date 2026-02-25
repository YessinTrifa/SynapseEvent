package com.synapseevent.service;

import com.synapseevent.entities.Booking;
import com.synapseevent.entities.User;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class BookingService implements IService<Booking> {
    private final MaConnection db = MaConnection.getInstance();
    private UserService userService = new UserService();

    @Override
    public boolean ajouter(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "INSERT INTO Booking (user_id, event_type, event_id, booking_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, booking.getUser().getId());
            stmt.setString(2, booking.getEventType());
            stmt.setLong(3, booking.getEventId());
            stmt.setDate(4, Date.valueOf(booking.getBookingDate()));
            stmt.setString(5, booking.getStatus());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Booking> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = userService.findbyId(rs.getLong("user_id"));
                Booking booking = new Booking(rs.getLong("id"), user, rs.getString("event_type"), rs.getLong("event_id"), rs.getDate("booking_date").toLocalDate(), rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return bookings;
    }

    @Override
    public Booking findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = userService.findbyId(rs.getLong("user_id"));
                return new Booking(rs.getLong("id"), user, rs.getString("event_type"), rs.getLong("event_id"), rs.getDate("booking_date").toLocalDate(), rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "UPDATE Booking SET user_id = ?, event_type = ?, event_id = ?, booking_date = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, booking.getUser().getId());
            stmt.setString(2, booking.getEventType());
            stmt.setLong(3, booking.getEventId());
            stmt.setDate(4, Date.valueOf(booking.getBookingDate()));
            stmt.setString(5, booking.getStatus());
            stmt.setLong(6, booking.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();
        if (booking.getId() != null) {
            String sql = "DELETE FROM Booking WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, booking.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<Booking> getBookingsByUser(User user) throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking(rs.getLong("id"), user, rs.getString("event_type"), rs.getLong("event_id"), rs.getDate("booking_date").toLocalDate(), rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return bookings;
    }

    public List<Booking> getBookingsByEvent(String eventType, Long eventId) throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE event_type = ? AND event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType);
            stmt.setLong(2, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = userService.findbyId(rs.getLong("user_id"));
                Booking booking = new Booking(rs.getLong("id"), user, rs.getString("event_type"), rs.getLong("event_id"), rs.getDate("booking_date").toLocalDate(), rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return bookings;
    }
}
