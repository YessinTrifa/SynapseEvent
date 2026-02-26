package com.synapseevent.service;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.MaConnection;
import com.synapseevent.utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {

    private final MaConnection db = MaConnection.getInstance();
    private final RoleService roleService = new RoleService();
    private final EntrepriseService entrepriseService = new EntrepriseService();

    @Override
    public boolean ajouter(User user) throws SQLException {
        Connection conn = db.requireConnection();

        if (user.getRoleId() == null) throw new SQLException("roleId is required");
        if (user.getEnterpriseId() == null) throw new SQLException("enterpriseId is required");

        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

        String sql = "INSERT INTO Utilisateur (email, password, nom, prenom, phone, address, profile_picture, role_id, enterprise_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getNom());
            stmt.setString(4, user.getPrenom());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getProfilePicture());
            stmt.setLong(8, user.getRoleId());
            stmt.setLong(9, user.getEnterpriseId());

            int res = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getLong(1));
            }

            return res > 0;
        }
    }

    @Override
    public List<User> readAll() throws SQLException {
        Connection conn = db.requireConnection();
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM Utilisateur";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(map(rs));
            }
        }

        return users;
    }

    @Override
    public User findbyId(Long id) throws SQLException {
        Connection conn = db.requireConnection();

        String sql = "SELECT * FROM Utilisateur WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
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

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user == null) return null;

        String stored = user.getPassword();
        if (stored == null) return null;

        boolean ok;
        if (stored.startsWith("$2")) {
            ok = PasswordUtil.verifyPassword(password, stored);
        } else {
            ok = password.equals(stored);
            if (ok) {
                try {
                    user.setPassword(password);
                    modifier(user);
                    user = findByEmail(email);
                } catch (Exception ignored) {}
            }
        }

        if (ok) {
            CurrentUser.setCurrentUser(user);
            return user;
        }
        return null;
    }

    public User login(String email) {
        User user = findByEmail(email);
        if (user != null) CurrentUser.setCurrentUser(user);
        return user;
    }

    @Override
    public boolean modifier(User user) throws SQLException {
        Connection conn = db.requireConnection();

        if (user.getId() == null) throw new SQLException("id is required");
        if (user.getRoleId() == null) throw new SQLException("roleId is required");
        if (user.getEnterpriseId() == null) throw new SQLException("enterpriseId is required");

        boolean updatePassword = user.getPassword() != null && !user.getPassword().isBlank();
        String sql = "UPDATE Utilisateur SET email = ?, nom = ?, prenom = ?, phone = ?, address = ?, profile_picture = ?, role_id = ?, enterprise_id = ?"
                + (updatePassword ? ", password = ?" : "")
                + " WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;

            stmt.setString(i++, user.getEmail());
            stmt.setString(i++, user.getNom());
            stmt.setString(i++, user.getPrenom());
            stmt.setString(i++, user.getPhone());
            stmt.setString(i++, user.getAddress());
            stmt.setString(i++, user.getProfilePicture());
            stmt.setLong(i++, user.getRoleId());
            stmt.setLong(i++, user.getEnterpriseId());

            if (updatePassword) {
                stmt.setString(i++, PasswordUtil.hashPassword(user.getPassword()));
            }

            stmt.setLong(i, user.getId());

            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    @Override
    public boolean supprimer(User user) throws SQLException {
        Connection conn = db.requireConnection();
        if (user == null || user.getId() == null) return false;

        String sql = "DELETE FROM Utilisateur WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        }
    }

    private User map(ResultSet rs) throws SQLException {

        Long roleId = rs.getLong("role_id");
        if (rs.wasNull()) roleId = null;

        Long enterpriseId = rs.getLong("enterprise_id");
        if (rs.wasNull()) enterpriseId = null;

        User user = new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("profile_picture"),
                roleId,
                enterpriseId
        );

        if (roleId != null) {
            Role role = roleService.findbyId(roleId);
            user.setRole(role);
        }

        if (enterpriseId != null) {
            Entreprise ent = entrepriseService.findbyId(enterpriseId);
            user.setEnterprise(ent);
        }

        return user;
    }
}