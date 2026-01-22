package com.synapseevent.dao;

import com.synapseevent.entities.Reservation;
import com.synapseevent.entities.User;
import com.synapseevent.entities.Event;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private Connection conn = MaConnection.getInstance().getConnection();
    private UserDAO userDAO = new UserDAO();
    private EventDAO eventDAO = new EventDAO();

    public void create(Reservation reservation) {
        String sql = "INSERT INTO Reservation (date, lieu, nombreParticipants, statut, utilisateur_id, evenement_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(reservation.getDate()));
            stmt.setString(2, reservation.getLieu());
            stmt.setInt(3, reservation.getNombreParticipants());
            stmt.setString(4, reservation.getStatut());
            stmt.setLong(5, reservation.getUtilisateur().getId());
            stmt.setLong(6, reservation.getEvenement().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                reservation.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> readAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservation";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = userDAO.readById(rs.getLong("utilisateur_id"));
                Event event = eventDAO.readById(rs.getLong("evenement_id"));
                Reservation reservation = new Reservation(rs.getLong("id"), rs.getDate("date").toLocalDate(), rs.getString("lieu"), rs.getInt("nombreParticipants"), rs.getString("statut"), user, event);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public Reservation readById(Long id) {
        String sql = "SELECT * FROM Reservation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = userDAO.readById(rs.getLong("utilisateur_id"));
                Event event = eventDAO.readById(rs.getLong("evenement_id"));
                return new Reservation(rs.getLong("id"), rs.getDate("date").toLocalDate(), rs.getString("lieu"), rs.getInt("nombreParticipants"), rs.getString("statut"), user, event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Reservation reservation) {
        String sql = "UPDATE Reservation SET date = ?, lieu = ?, nombreParticipants = ?, statut = ?, utilisateur_id = ?, evenement_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(reservation.getDate()));
            stmt.setString(2, reservation.getLieu());
            stmt.setInt(3, reservation.getNombreParticipants());
            stmt.setString(4, reservation.getStatut());
            stmt.setLong(5, reservation.getUtilisateur().getId());
            stmt.setLong(6, reservation.getEvenement().getId());
            stmt.setLong(7, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Reservation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}