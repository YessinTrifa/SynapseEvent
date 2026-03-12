package com.synapseevent.service;

import com.synapseevent.entities.ServiceFeeRate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFeeRateService {
    @SuppressWarnings("static-access")
    private Connection conn() {
        return com.synapseevent.utils.MaConnection.getInstance().getConnection();
    }

    public List<ServiceFeeRate> readAll() {
        List<ServiceFeeRate> feeRates = new ArrayList<>();
        String query = "SELECT * FROM service_fee_rates WHERE is_active = true ORDER BY rate ASC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                ServiceFeeRate feeRate = mapResultSetToServiceFeeRate(rs);
                feeRates.add(feeRate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feeRates;
    }

    public ServiceFeeRate findById(Long id) {
        String query = "SELECT * FROM service_fee_rates WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToServiceFeeRate(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<ServiceFeeRate> findByEventType(String eventType) {
        List<ServiceFeeRate> feeRates = new ArrayList<>();
        String query = "SELECT * FROM service_fee_rates WHERE is_active = true AND " +
                     "(event_type = ? OR event_type = 'ALL' OR event_type IS NULL) " +
                     "ORDER BY rate ASC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, eventType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ServiceFeeRate feeRate = mapResultSetToServiceFeeRate(rs);
                    if (feeRate.appliesToEventType(eventType)) {
                        feeRates.add(feeRate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feeRates;
    }

    public List<ServiceFeeRate> findByApplicability(String applicability) {
        List<ServiceFeeRate> feeRates = new ArrayList<>();
        String query = "SELECT * FROM service_fee_rates WHERE is_active = true AND " +
                     "applicability = ? ORDER BY rate ASC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, applicability);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ServiceFeeRate feeRate = mapResultSetToServiceFeeRate(rs);
                    feeRates.add(feeRate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feeRates;
    }

    public boolean ajouter(ServiceFeeRate feeRate) {
        String query = "INSERT INTO service_fee_rates (name, rate, type, applicability, " +
                     "event_type, min_amount, max_amount, start_date, end_date, " +
                     "is_active, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, feeRate.getName());
            pstmt.setDouble(2, feeRate.getRate());
            pstmt.setString(3, feeRate.getType());
            pstmt.setString(4, feeRate.getApplicability());
            pstmt.setString(5, feeRate.getEventType());
            pstmt.setDouble(6, feeRate.getMinAmount());
            pstmt.setDouble(7, feeRate.getMaxAmount());
            pstmt.setDate(8, feeRate.getStartDate() != null ? Date.valueOf(feeRate.getStartDate()) : null);
            pstmt.setDate(9, feeRate.getEndDate() != null ? Date.valueOf(feeRate.getEndDate()) : null);
            pstmt.setBoolean(10, feeRate.getIsActive());
            pstmt.setString(11, feeRate.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(ServiceFeeRate feeRate) {
        String query = "UPDATE service_fee_rates SET name = ?, rate = ?, type = ?, " +
                     "applicability = ?, event_type = ?, min_amount = ?, max_amount = ?, " +
                     "start_date = ?, end_date = ?, is_active = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, feeRate.getName());
            pstmt.setDouble(2, feeRate.getRate());
            pstmt.setString(3, feeRate.getType());
            pstmt.setString(4, feeRate.getApplicability());
            pstmt.setString(5, feeRate.getEventType());
            pstmt.setDouble(6, feeRate.getMinAmount());
            pstmt.setDouble(7, feeRate.getMaxAmount());
            pstmt.setDate(8, feeRate.getStartDate() != null ? Date.valueOf(feeRate.getStartDate()) : null);
            pstmt.setDate(9, feeRate.getEndDate() != null ? Date.valueOf(feeRate.getEndDate()) : null);
            pstmt.setBoolean(10, feeRate.getIsActive());
            pstmt.setString(11, feeRate.getDescription());
            pstmt.setLong(12, feeRate.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(ServiceFeeRate feeRate) {
        String query = "DELETE FROM service_fee_rates WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, feeRate.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double getTotalServiceFeeRate(String eventType) {
        String query = "SELECT COALESCE(SUM(rate), 0) as total_rate FROM service_fee_rates " +
                     "WHERE is_active = true AND (event_type = ? OR event_type = 'ALL' OR event_type IS NULL)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, eventType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_rate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }

    private ServiceFeeRate mapResultSetToServiceFeeRate(ResultSet rs) throws SQLException {
        ServiceFeeRate feeRate = new ServiceFeeRate();
        
        feeRate.setId(rs.getLong("id"));
        feeRate.setName(rs.getString("name"));
        feeRate.setRate(rs.getDouble("rate"));
        feeRate.setType(rs.getString("type"));
        feeRate.setApplicability(rs.getString("applicability"));
        feeRate.setEventType(rs.getString("event_type"));
        feeRate.setMinAmount(rs.getDouble("min_amount"));
        feeRate.setMaxAmount(rs.getDouble("max_amount"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            feeRate.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            feeRate.setEndDate(endDate.toLocalDate());
        }
        
        feeRate.setIsActive(rs.getBoolean("is_active"));
        feeRate.setDescription(rs.getString("description"));
        
        return feeRate;
    }
}
