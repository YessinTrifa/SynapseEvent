package com.synapseevent.dao;

import com.synapseevent.entities.Participant;
import com.synapseevent.entities.User;
import com.synapseevent.entities.Reservation;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDAO {
    private Connection conn = MaConnection.getInstance().getConnection();
    private UserDAO userDAO = new UserDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    public void create(Participant participant) {
        String sql = "INSERT INTO Participant (utilisateur_id, reservation_id, statut) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, participant.getUtilisateur().getId());
            stmt.setLong(2, participant.getReservation().getId());
            stmt.setString(3, participant.getStatut());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                participant.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Participant> readAll() {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT * FROM Participant";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = userDAO.readById(rs.getLong("utilisateur_id"));
                Reservation reservation = reservationDAO.readById(rs.getLong("reservation_id"));
                Participant participant = new Participant(rs.getLong("id"), user, reservation, rs.getString("statut"));
                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public Participant readById(Long id) {
        String sql = "SELECT * FROM Participant WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = userDAO.readById(rs.getLong("utilisateur_id"));
                Reservation reservation = reservationDAO.readById(rs.getLong("reservation_id"));
                return new Participant(rs.getLong("id"), user, reservation, rs.getString("statut"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Participant participant) {
        String sql = "UPDATE Participant SET utilisateur_id = ?, reservation_id = ?, statut = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, participant.getUtilisateur().getId());
            stmt.setLong(2, participant.getReservation().getId());
            stmt.setString(3, participant.getStatut());
            stmt.setLong(4, participant.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Participant WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}