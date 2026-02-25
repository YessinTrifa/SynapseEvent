package com.synapseevent.service;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseService {
    private final MaConnection db = MaConnection.getInstance();

    public void add(Entreprise entreprise) {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = "INSERT INTO Enterprise (nom, siret) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entreprise.getNom());
            stmt.setString(2, entreprise.getSiret());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                entreprise.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Entreprise> getAll() {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<Entreprise> entreprises = new ArrayList<>();
        String sql = "SELECT * FROM Enterprise";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Entreprise entreprise = new Entreprise(rs.getLong("id"), rs.getString("nom"), rs.getString("siret"));
                entreprises.add(entreprise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entreprises;
    }

    public Entreprise getById(Long id) {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String sql = "SELECT * FROM Enterprise WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Entreprise(rs.getLong("id"), rs.getString("nom"), rs.getString("siret"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entreprise findbyId(Long id) {
        return getById(id);
    }

    public void update(Entreprise entreprise) {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = "UPDATE Enterprise SET nom = ?, siret = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entreprise.getNom());
            stmt.setString(2, entreprise.getSiret());
            stmt.setLong(3, entreprise.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        Connection conn;
        try {
            conn = db.requireConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = "DELETE FROM Enterprise WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


