package com.synapseevent.service;

import com.synapseevent.entities.CustomEventRequest;
import com.synapseevent.entities.User;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class CustomEventRequestService implements IService<CustomEventRequest> {
    private Connection conn = MaConnection.getInstance().getConnection();
    private UserService userService = new UserService();

    @Override
    public boolean ajouter(CustomEventRequest request) throws SQLException {
        String sql = "INSERT INTO CustomEventRequest (user_id, event_type, event_date, description, status, created_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, request.getUser().getId());
            stmt.setString(2, request.getEventType());
            stmt.setDate(3, Date.valueOf(request.getEventDate()));
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setDate(6, Date.valueOf(request.getCreatedDate()));
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                request.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CustomEventRequest> readAll() throws SQLException {
        List<CustomEventRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM CustomEventRequest";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = userService.findbyId(rs.getLong("user_id"));
                CustomEventRequest request = new CustomEventRequest(rs.getLong("id"), user, rs.getString("event_type"), rs.getDate("event_date").toLocalDate(), rs.getString("description"), rs.getString("status"), rs.getDate("created_date").toLocalDate());
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return requests;
    }

    @Override
    public CustomEventRequest findbyId(Long id) throws SQLException {
        String sql = "SELECT * FROM CustomEventRequest WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = userService.findbyId(rs.getLong("user_id"));
                return new CustomEventRequest(rs.getLong("id"), user, rs.getString("event_type"), rs.getDate("event_date").toLocalDate(), rs.getString("description"), rs.getString("status"), rs.getDate("created_date").toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Override
    public boolean modifier(CustomEventRequest request) throws SQLException {
        String sql = "UPDATE CustomEventRequest SET user_id = ?, event_type = ?, event_date = ?, description = ?, status = ?, created_date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, request.getUser().getId());
            stmt.setString(2, request.getEventType());
            stmt.setDate(3, Date.valueOf(request.getEventDate()));
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setDate(6, Date.valueOf(request.getCreatedDate()));
            stmt.setLong(7, request.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(CustomEventRequest request) throws SQLException {
        if (request.getId() != null) {
            String sql = "DELETE FROM CustomEventRequest WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, request.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    public List<CustomEventRequest> getRequestsByUser(User user) throws SQLException {
        List<CustomEventRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM CustomEventRequest WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CustomEventRequest request = new CustomEventRequest(rs.getLong("id"), user, rs.getString("event_type"), rs.getDate("event_date").toLocalDate(), rs.getString("description"), rs.getString("status"), rs.getDate("created_date").toLocalDate());
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return requests;
    }

    public boolean updateStatus(Long id, String status) throws SQLException {
        String sql = "UPDATE CustomEventRequest SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, id);
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}