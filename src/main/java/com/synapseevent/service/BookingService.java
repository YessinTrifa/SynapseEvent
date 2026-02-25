package com.synapseevent.service;

import com.synapseevent.entities.Booking;
import com.synapseevent.entities.User;
import com.synapseevent.utils.MaConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService implements IService<Booking> {

    private final MaConnection db = MaConnection.getInstance();
    private final UserService userService = new UserService();

    @Override
    public boolean ajouter(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();

        if (booking.getUserId() == null) throw new SQLException("userId is required for Booking");

        String sql = "INSERT INTO Booking (user_id, event_type, event_id, booking_date, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, booking.getUserId());
            stmt.setString(2, booking.getEventType());
            stmt.setLong(3, booking.getEventId());

            if (booking.getBookingDate() == null) stmt.setNull(4, Types.DATE);
            else stmt.setDate(4, Date.valueOf(booking.getBookingDate()));

            stmt.setString(5, booking.getStatus());

            int res = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) booking.setId(rs.getLong(1));
            }

            return res > 0;
        }
    }

    @Override
    public List<Booking> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(map(rs));
            }
        }

        return bookings;
    }

    @Override
    public Booking findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "SELECT * FROM Booking WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    @Override
    public boolean modifier(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();

        if (booking.getId() == null) throw new SQLException("id is required for update");
        if (booking.getUserId() == null) throw new SQLException("userId is required for update");

        String sql = "UPDATE Booking SET user_id = ?, event_type = ?, event_id = ?, booking_date = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, booking.getUserId());
            stmt.setString(2, booking.getEventType());
            stmt.setLong(3, booking.getEventId());

            if (booking.getBookingDate() == null) stmt.setNull(4, Types.DATE);
            else stmt.setDate(4, Date.valueOf(booking.getBookingDate()));

            stmt.setString(5, booking.getStatus());
            stmt.setLong(6, booking.getId());

            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean supprimer(Booking booking) throws SQLException {
        Connection conn = db.requireConnection();
        if (booking == null || booking.getId() == null) return false;

        String sql = "DELETE FROM Booking WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, booking.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    public List<Booking> getBookingsByUserId(Long userId) throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = map(rs);
                    bookings.add(b);
                }
            }
        }

        return bookings;
    }

    public List<Booking> getBookingsByUser(User user) throws SQLException {
        if (user == null || user.getId() == null) return new ArrayList<>();
        return getBookingsByUserId(user.getId());
    }

    public List<Booking> getBookingsByEvent(String eventType, Long eventId) throws SQLException {
        Connection conn = db.requireConnection();
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking WHERE event_type = ? AND event_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType);
            stmt.setLong(2, eventId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(map(rs));
                }
            }
        }

        return bookings;
    }

    private Booking map(ResultSet rs) throws SQLException {

        Long userId = rs.getLong("user_id");
        if (rs.wasNull()) userId = null;

        Booking booking = new Booking(
                rs.getLong("id"),
                userId,
                rs.getString("event_type"),
                rs.getLong("event_id"),
                rs.getDate("booking_date") != null ? rs.getDate("booking_date").toLocalDate() : null,
                rs.getString("status")
        );

        if (userId != null) {
            User user = userService.findbyId(userId);
            booking.setUser(user);
        }

        return booking;
    }
}