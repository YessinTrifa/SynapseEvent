package com.synapseevent.service;

import com.synapseevent.entities.User;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import com.synapseevent.utils.CurrentUser;

public class UserService implements IService<User> {
    private Connection conn = MaConnection.getInstance().getConnection();
    private RoleService roleService = new RoleService();
    private EntrepriseService entrepriseService = new EntrepriseService();

    @Override
    public boolean ajouter(User user) throws SQLException {
        String sql = "INSERT INTO Utilisateur (email, nom, prenom, role_id, enterprise_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getNom());
            stmt.setString(3, user.getPrenom());
            stmt.setLong(4, user.getRole().getId());
            stmt.setLong(5, user.getEnterprise().getId());
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
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                User user = new User(rs.getLong("id"), rs.getString("email"), rs.getString("nom"), rs.getString("prenom"), role, entreprise);
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
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("nom"), rs.getString("prenom"), role, entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = roleService.findbyId(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseService.findbyId(rs.getLong("enterprise_id"));
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("nom"), rs.getString("prenom"), role, entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        String sql = "UPDATE Utilisateur SET email = ?, nom = ?, prenom = ?, role_id = ?, enterprise_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getNom());
            stmt.setString(3, user.getPrenom());
            stmt.setLong(4, user.getRole().getId());
            stmt.setLong(5, user.getEnterprise().getId());
            stmt.setLong(6, user.getId());
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean supprimer(User user) throws SQLException {
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