package com.synapseevent.service;

import com.synapseevent.entities.User;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.MaConnection;
import com.synapseevent.utils.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import com.synapseevent.utils.CurrentUser;

public class UserService implements IService<User> {
    private final MaConnection db = MaConnection.getInstance();
    private RoleService roleService = new RoleService();
    private EntrepriseService entrepriseService = new EntrepriseService();

    @Override
    public boolean ajouter(User user) throws SQLException {
        Connection conn = db.requireConnection();
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        String sql = "INSERT INTO Utilisateur (email, password, nom, prenom, phone, address, profile_picture, role_id, enterprise_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getNom());
            stmt.setString(4, user.getPrenom());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getProfilePicture());
            stmt.setLong(8, user.getRole().getId());
            stmt.setLong(9, user.getEnterprise().getId());
            int res = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<User> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                User user = new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("nom"), rs.getString("prenom"), rs.getString("phone"), rs.getString("address"), rs.getString("profile_picture"), role, entreprise);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return users;
    }

    @Override
    public User findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("nom"), rs.getString("prenom"), rs.getString("phone"), rs.getString("address"), rs.getString("profile_picture"), role, entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public User findByEmail(String email) {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("nom"), rs.getString("prenom"), rs.getString("phone"), rs.getString("address"), rs.getString("profile_picture"), role, entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user != null) {
            String storedPassword = user.getPassword();
            // Check if password is hashed (starts with $2a$) or plain text
            boolean passwordValid = false;
            if (storedPassword != null && storedPassword.startsWith("$2a$")) {
                // Hashed password
                passwordValid = PasswordUtil.verifyPassword(password, storedPassword);
            } else {
                // Plain text password (for backward compatibility)
                passwordValid = password.equals(storedPassword);
            }
            if (passwordValid) {
                CurrentUser.setCurrentUser(user);
                return user;
            }
        }
        return null;
    }

    public User login(String email) {
        User user = findByEmail(email);
        if (user != null) {
            CurrentUser.setCurrentUser(user);
        }
        return user;
    }

    @Override
    public boolean modifier(User user) throws SQLException {
        Connection conn = db.requireConnection();
        String hashedPassword = user.getPassword() != null && !user.getPassword().isEmpty() ? PasswordUtil.hashPassword(user.getPassword()) : null;
        String sql = "UPDATE Utilisateur SET email = ?, nom = ?, prenom = ?, phone = ?, address = ?, profile_picture = ?, role_id = ?, enterprise_id = ?" + (hashedPassword != null ? ", password = ?" : "") + " WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, user.getEmail());
            stmt.setString(paramIndex++, user.getNom());
            stmt.setString(paramIndex++, user.getPrenom());
            stmt.setString(paramIndex++, user.getPhone());
            stmt.setString(paramIndex++, user.getAddress());
            stmt.setString(paramIndex++, user.getProfilePicture());
            stmt.setLong(paramIndex++, user.getRole().getId());
            stmt.setLong(paramIndex++, user.getEnterprise().getId());
            if (hashedPassword != null) {
                stmt.setString(paramIndex++, hashedPassword);
            }
            stmt.setLong(paramIndex, user.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(User user) throws SQLException {
        Connection conn = db.requireConnection();
        if (user.getId() != null) {
            String sql = "DELETE FROM Utilisateur WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, user.getId());
                int res = stmt.executeUpdate();
                return res > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }
}
