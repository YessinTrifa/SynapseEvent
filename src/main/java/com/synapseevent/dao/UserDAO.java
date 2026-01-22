package com.synapseevent.dao;

import com.synapseevent.entities.User;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn = MaConnection.getInstance().getConnection();
    private RoleDAO roleDAO = new RoleDAO();
    private EntrepriseDAO entrepriseDAO = new EntrepriseDAO();

    public boolean create(User user) {
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
            return false;
        }
    }

    public List<User> readAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = roleDAO.readById(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseDAO.readById(rs.getLong("enterprise_id"));
                User user = new User(rs.getLong("id"), rs.getString("email"), rs.getString("nom"), rs.getString("prenom"), role, entreprise);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User readById(Long id) {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Role role = roleDAO.readById(rs.getLong("role_id"));
                Entreprise entreprise = entrepriseDAO.readById(rs.getLong("enterprise_id"));
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("nom"), rs.getString("prenom"), role, entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(User user) {
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
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int res = stmt.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}