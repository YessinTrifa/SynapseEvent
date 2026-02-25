package com.synapseevent.service;

import com.synapseevent.entities.CustomEventRequest;
import com.synapseevent.entities.User;
import com.synapseevent.utils.MaConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class CustomEventRequestService implements IService<CustomEventRequest> {

    private final MaConnection db = MaConnection.getInstance();
    private final UserService userService = new UserService();
    private Connection conn = MaConnection.getInstance().getConnection();
    @Override
    public boolean ajouter(CustomEventRequest request) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "INSERT INTO CustomEventRequest " +
                "(user_id, event_type, event_date, description, status, created_date, budget, capacity, location, reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (request.getUserId() == null) throw new SQLException("userId is required");

            stmt.setLong(1, request.getUserId());
            stmt.setString(2, request.getEventType());
            stmt.setDate(3, request.getEventDate() != null ? Date.valueOf(request.getEventDate()) : null);
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setDate(6, request.getCreatedDate() != null ? Date.valueOf(request.getCreatedDate()) : null);

            if (request.getBudget() == null) stmt.setNull(7, Types.DOUBLE);
            else stmt.setDouble(7, request.getBudget());

            if (request.getCapacity() == null) stmt.setNull(8, Types.INTEGER);
            else stmt.setInt(8, request.getCapacity());

            stmt.setString(9, request.getLocation());
            stmt.setString(10, request.getReason());

            int res = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) request.setId(rs.getLong(1));
            }

            return res > 0;
        }
    }

    @Override
    public List<CustomEventRequest> readAll() throws SQLException {
        List<CustomEventRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM custom_event_request"; // use your real table name

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CustomEventRequest request = new CustomEventRequest(); // ✅ MUST exist

                request.setId(rs.getLong("id"));
                request.setEventType(rs.getString("event_type"));
                request.setEventDate(rs.getDate("event_date") != null ? rs.getDate("event_date").toLocalDate() : null);

                // ✅ Budget DECIMAL -> Double safely
                BigDecimal bd = rs.getBigDecimal("budget");
                request.setBudget(bd == null ? null : bd.doubleValue());

                request.setCapacity(rs.getInt("capacity"));
                if (rs.wasNull()) request.setCapacity(null); // only if capacity is Integer in entity

                request.setLocation(rs.getString("location"));
                request.setDescription(rs.getString("description"));
                request.setReason(rs.getString("reason"));
                request.setStatus(rs.getString("status"));

                // if you have user_id:
                long userId = rs.getLong("user_id");
                if (!rs.wasNull()) {
                    User u = userService.findbyId(userId); // adapt to your method name
                    request.setUser(u);
                }

                list.add(request);
            }
        }

        return list;
    }

    @Override
    public CustomEventRequest findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "SELECT * FROM CustomEventRequest WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                Long userId = rs.getLong("user_id");
                if (rs.wasNull()) userId = null;

                Double budget = (Double) rs.getObject("budget");
                Integer capacity = (Integer) rs.getObject("capacity");

                CustomEventRequest request = new CustomEventRequest(
                        rs.getLong("id"),
                        userId,
                        rs.getString("event_type"),
                        rs.getDate("event_date") != null ? rs.getDate("event_date").toLocalDate() : null,
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getDate("created_date") != null ? rs.getDate("created_date").toLocalDate() : null,
                        budget,
                        capacity,
                        rs.getString("location"),
                        rs.getString("reason")
                );

                if (userId != null) {
                    User user = userService.findbyId(userId);
                    request.setUser(user);
                }

                return request;
            }
        }
    }

    @Override
    public boolean modifier(CustomEventRequest request) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "UPDATE CustomEventRequest SET user_id = ?, event_type = ?, event_date = ?, description = ?, " +
                "status = ?, created_date = ?, budget = ?, capacity = ?, location = ?, reason = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (request.getId() == null) throw new SQLException("id is required");
            if (request.getUserId() == null) throw new SQLException("userId is required");

            stmt.setLong(1, request.getUserId());
            stmt.setString(2, request.getEventType());
            stmt.setDate(3, request.getEventDate() != null ? Date.valueOf(request.getEventDate()) : null);
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setDate(6, request.getCreatedDate() != null ? Date.valueOf(request.getCreatedDate()) : null);

            if (request.getBudget() == null) stmt.setNull(7, Types.DOUBLE);
            else stmt.setDouble(7, request.getBudget());

            if (request.getCapacity() == null) stmt.setNull(8, Types.INTEGER);
            else stmt.setInt(8, request.getCapacity());

            stmt.setString(9, request.getLocation());
            stmt.setString(10, request.getReason());
            stmt.setLong(11, request.getId());

            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean supprimer(CustomEventRequest request) throws SQLException {
        Connection conn = db.requireConnection();
        if (request == null || request.getId() == null) return false;

        String sql = "DELETE FROM CustomEventRequest WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, request.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    public List<CustomEventRequest> getRequestsByUserId(Long userId) throws SQLException {
        Connection conn = db.requireConnection();
        List<CustomEventRequest> requests = new ArrayList<>();

        String sql = "SELECT * FROM CustomEventRequest WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Double budget = (Double) rs.getObject("budget");
                    Integer capacity = (Integer) rs.getObject("capacity");

                    CustomEventRequest request = new CustomEventRequest(
                            rs.getLong("id"),
                            userId,
                            rs.getString("event_type"),
                            rs.getDate("event_date") != null ? rs.getDate("event_date").toLocalDate() : null,
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getDate("created_date") != null ? rs.getDate("created_date").toLocalDate() : null,
                            budget,
                            capacity,
                            rs.getString("location"),
                            rs.getString("reason")
                    );

                    User user = userService.findbyId(userId);
                    request.setUser(user);

                    requests.add(request);
                }
            }
        }

        return requests;
    }

    public List<CustomEventRequest> getRequestsByUser(User user) throws SQLException {
        if (user == null || user.getId() == null) return new ArrayList<>();
        return getRequestsByUserId(user.getId());
    }

    public boolean updateStatus(Long id, String status, String reason) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "UPDATE CustomEventRequest SET status = ?, reason = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, reason);
            stmt.setLong(3, id);
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }
}