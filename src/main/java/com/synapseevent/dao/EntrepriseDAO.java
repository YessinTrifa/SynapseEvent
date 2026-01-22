package com.synapseevent.dao;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseDAO {
    private Connection conn = MaConnection.getInstance().getConnection();

    public void create(Entreprise entreprise) {
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

    public List<Entreprise> readAll() {
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

    public Entreprise readById(Long id) {
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

    public void update(Entreprise entreprise) {
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
        String sql = "DELETE FROM Enterprise WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}