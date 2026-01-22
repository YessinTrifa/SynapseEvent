package com.synapseevent.dao;

import com.synapseevent.entities.Option;
import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDAO {
    private Connection conn = MaConnection.getInstance().getConnection();
    private EventDAO eventDAO = new EventDAO();

    public void create(Option option) {
        String sql = "INSERT INTO Option (nom, prix, statut, evenement_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, option.getNom());
            stmt.setBigDecimal(2, option.getPrix());
            stmt.setString(3, option.getStatut());
            stmt.setLong(4, option.getEvenement().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                option.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Option> readAll() {
        List<Option> options = new ArrayList<>();
        String sql = "SELECT * FROM Option";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event event = eventDAO.readById(rs.getLong("evenement_id"));
                Option option = new Option(rs.getLong("id"), rs.getString("nom"), rs.getBigDecimal("prix"), rs.getString("statut"), event);
                options.add(option);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }

    public Option readById(Long id) {
        String sql = "SELECT * FROM Option WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Event event = eventDAO.readById(rs.getLong("evenement_id"));
                return new Option(rs.getLong("id"), rs.getString("nom"), rs.getBigDecimal("prix"), rs.getString("statut"), event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Option option) {
        String sql = "UPDATE Option SET nom = ?, prix = ?, statut = ?, evenement_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, option.getNom());
            stmt.setBigDecimal(2, option.getPrix());
            stmt.setString(3, option.getStatut());
            stmt.setLong(4, option.getEvenement().getId());
            stmt.setLong(5, option.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Option WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}