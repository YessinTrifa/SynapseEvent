package com.synapseevent.dao;

import com.synapseevent.entities.Payment;
import com.synapseevent.entities.Reservation;
import com.synapseevent.utils.MaConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private Connection conn = MaConnection.getInstance().getConnection();
    private ReservationDAO reservationDAO = new ReservationDAO();

    public void create(Payment payment) {
        String sql = "INSERT INTO Paiement (montant, statut, datePaiement, reservation_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, payment.getMontant());
            stmt.setString(2, payment.getStatut());
            stmt.setDate(3, payment.getDatePaiement() != null ? Date.valueOf(payment.getDatePaiement()) : null);
            stmt.setLong(4, payment.getReservation().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                payment.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Payment> readAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Paiement";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation reservation = reservationDAO.readById(rs.getLong("reservation_id"));
                Date date = rs.getDate("datePaiement");
                Payment payment = new Payment(rs.getLong("id"), rs.getBigDecimal("montant"), rs.getString("statut"), date != null ? date.toLocalDate() : null, reservation);
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public Payment readById(Long id) {
        String sql = "SELECT * FROM Paiement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Reservation reservation = reservationDAO.readById(rs.getLong("reservation_id"));
                Date date = rs.getDate("datePaiement");
                return new Payment(rs.getLong("id"), rs.getBigDecimal("montant"), rs.getString("statut"), date != null ? date.toLocalDate() : null, reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Payment payment) {
        String sql = "UPDATE Paiement SET montant = ?, statut = ?, datePaiement = ?, reservation_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, payment.getMontant());
            stmt.setString(2, payment.getStatut());
            stmt.setDate(3, payment.getDatePaiement() != null ? Date.valueOf(payment.getDatePaiement()) : null);
            stmt.setLong(4, payment.getReservation().getId());
            stmt.setLong(5, payment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Paiement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}