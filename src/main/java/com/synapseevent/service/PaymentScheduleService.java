package com.synapseevent.service;

import com.synapseevent.entities.PaymentSchedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentScheduleService {
    private Connection conn() {
        return com.synapseevent.utils.MaConnection.getInstance().getConnection();
    }

    public List<PaymentSchedule> findByBookingId(Long bookingId) {
        List<PaymentSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM payment_schedules WHERE booking_id = ? ORDER BY installment_number";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentSchedule schedule = mapResultSetToPaymentSchedule(rs);
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return schedules;
    }

    public PaymentSchedule findById(Long id) {
        String query = "SELECT * FROM payment_schedules WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentSchedule(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<PaymentSchedule> findOverduePayments() {
        List<PaymentSchedule> overduePayments = new ArrayList<>();
        String query = "SELECT * FROM payment_schedules WHERE status = 'PENDING' " +
                     "AND due_date < CURDATE() ORDER BY due_date ASC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PaymentSchedule schedule = mapResultSetToPaymentSchedule(rs);
                if (schedule.isOverdue()) {
                    schedule.markAsOverdue();
                    updateStatus(schedule);
                    overduePayments.add(schedule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return overduePayments;
    }

    public List<PaymentSchedule> findUpcomingPayments(int daysAhead) {
        List<PaymentSchedule> upcomingPayments = new ArrayList<>();
        String query = "SELECT * FROM payment_schedules WHERE status = 'PENDING' " +
                     "AND due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                     "ORDER BY due_date ASC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setInt(1, daysAhead);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentSchedule schedule = mapResultSetToPaymentSchedule(rs);
                    upcomingPayments.add(schedule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return upcomingPayments;
    }

    public boolean ajouter(PaymentSchedule schedule) {
        String query = "INSERT INTO payment_schedules (booking_id, installment_number, " +
                     "amount, due_date, status, description, is_deposit, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, schedule.getBookingId());
            pstmt.setInt(2, schedule.getInstallmentNumber());
            pstmt.setDouble(3, schedule.getAmount());
            pstmt.setDate(4, schedule.getDueDate() != null ? Date.valueOf(schedule.getDueDate()) : null);
            pstmt.setString(5, schedule.getStatus());
            pstmt.setString(6, schedule.getDescription());
            pstmt.setBoolean(7, schedule.getIsDeposit());
            pstmt.setTimestamp(8, Timestamp.valueOf(schedule.getCreatedAt()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ajouterMultiple(List<PaymentSchedule> schedules) {
        String query = "INSERT INTO payment_schedules (booking_id, installment_number, " +
                     "amount, due_date, status, description, is_deposit, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            for (PaymentSchedule schedule : schedules) {
                pstmt.setLong(1, schedule.getBookingId());
                pstmt.setInt(2, schedule.getInstallmentNumber());
                pstmt.setDouble(3, schedule.getAmount());
                pstmt.setDate(4, schedule.getDueDate() != null ? Date.valueOf(schedule.getDueDate()) : null);
                pstmt.setString(5, schedule.getStatus());
                pstmt.setString(6, schedule.getDescription());
                pstmt.setBoolean(7, schedule.getIsDeposit());
                pstmt.setTimestamp(8, Timestamp.valueOf(schedule.getCreatedAt()));
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(PaymentSchedule schedule) {
        String query = "UPDATE payment_schedules SET status = ?, paid_date = ?, " +
                     "payment_method = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, schedule.getStatus());
            pstmt.setDate(2, schedule.getPaidDate() != null ? Date.valueOf(schedule.getPaidDate()) : null);
            pstmt.setString(3, schedule.getPaymentMethod());
            pstmt.setLong(4, schedule.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAsPaid(Long scheduleId, String paymentMethod) {
        String query = "UPDATE payment_schedules SET status = 'PAID', paid_date = CURDATE(), " +
                     "payment_method = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, paymentMethod);
            pstmt.setLong(2, scheduleId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReminderSent(Long scheduleId) {
        String query = "UPDATE payment_schedules SET reminder_sent = NOW() WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, scheduleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(PaymentSchedule schedule) {
        String query = "DELETE FROM payment_schedules WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, schedule.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double getTotalPaidAmount(Long bookingId) {
        String query = "SELECT COALESCE(SUM(amount), 0) as total_paid FROM payment_schedules " +
                     "WHERE booking_id = ? AND status = 'PAID'";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_paid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }

    public Double getTotalDueAmount(Long bookingId) {
        String query = "SELECT COALESCE(SUM(amount), 0) as total_due FROM payment_schedules " +
                     "WHERE booking_id = ? AND status = 'PENDING'";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_due");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }

    private PaymentSchedule mapResultSetToPaymentSchedule(ResultSet rs) throws SQLException {
        PaymentSchedule schedule = new PaymentSchedule();
        
        schedule.setId(rs.getLong("id"));
        schedule.setBookingId(rs.getLong("booking_id"));
        schedule.setInstallmentNumber(rs.getInt("installment_number"));
        schedule.setAmount(rs.getDouble("amount"));
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            schedule.setDueDate(dueDate.toLocalDate());
        }
        
        Date paidDate = rs.getDate("paid_date");
        if (paidDate != null) {
            schedule.setPaidDate(paidDate.toLocalDate());
        }
        
        schedule.setStatus(rs.getString("status"));
        schedule.setPaymentMethod(rs.getString("payment_method"));
        schedule.setDescription(rs.getString("description"));
        schedule.setIsDeposit(rs.getBoolean("is_deposit"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            schedule.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp reminderSent = rs.getTimestamp("reminder_sent");
        if (reminderSent != null) {
            schedule.setReminderSent(reminderSent.toLocalDateTime());
        }
        
        return schedule;
    }
}
