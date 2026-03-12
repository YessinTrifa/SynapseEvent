package com.synapseevent.service;

import com.synapseevent.entities.TaxRate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaxRateService {
    @SuppressWarnings("static-access")
    private Connection conn() {
        return com.synapseevent.utils.MaConnection.getInstance().getConnection();
    }

    public List<TaxRate> readAll() {
        List<TaxRate> taxRates = new ArrayList<>();
        String query = "SELECT * FROM tax_rates WHERE is_active = true ORDER BY rate ASC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                TaxRate taxRate = mapResultSetToTaxRate(rs);
                taxRates.add(taxRate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return taxRates;
    }

    public TaxRate findById(Long id) {
        String query = "SELECT * FROM tax_rates WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTaxRate(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<TaxRate> findByEventType(String eventType) {
        List<TaxRate> taxRates = new ArrayList<>();
        String query = "SELECT * FROM tax_rates WHERE is_active = true AND " +
                     "(event_type = ? OR event_type = 'ALL' OR event_type IS NULL) " +
                     "ORDER BY rate ASC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, eventType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TaxRate taxRate = mapResultSetToTaxRate(rs);
                    if (taxRate.appliesToEventType(eventType)) {
                        taxRates.add(taxRate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return taxRates;
    }

    public List<TaxRate> findByApplicability(String applicability) {
        List<TaxRate> taxRates = new ArrayList<>();
        String query = "SELECT * FROM tax_rates WHERE is_active = true AND " +
                     "applicability = ? ORDER BY rate ASC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, applicability);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TaxRate taxRate = mapResultSetToTaxRate(rs);
                    taxRates.add(taxRate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return taxRates;
    }

    public boolean ajouter(TaxRate taxRate) {
        String query = "INSERT INTO tax_rates (name, rate, type, applicability, " +
                     "event_type, start_date, end_date, is_active, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, taxRate.getName());
            pstmt.setDouble(2, taxRate.getRate());
            pstmt.setString(3, taxRate.getType());
            pstmt.setString(4, taxRate.getApplicability());
            pstmt.setString(5, taxRate.getEventType());
            pstmt.setDate(6, taxRate.getStartDate() != null ? Date.valueOf(taxRate.getStartDate()) : null);
            pstmt.setDate(7, taxRate.getEndDate() != null ? Date.valueOf(taxRate.getEndDate()) : null);
            pstmt.setBoolean(8, taxRate.getIsActive());
            pstmt.setString(9, taxRate.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(TaxRate taxRate) {
        String query = "UPDATE tax_rates SET name = ?, rate = ?, type = ?, " +
                     "applicability = ?, event_type = ?, start_date = ?, " +
                     "end_date = ?, is_active = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, taxRate.getName());
            pstmt.setDouble(2, taxRate.getRate());
            pstmt.setString(3, taxRate.getType());
            pstmt.setString(4, taxRate.getApplicability());
            pstmt.setString(5, taxRate.getEventType());
            pstmt.setDate(6, taxRate.getStartDate() != null ? Date.valueOf(taxRate.getStartDate()) : null);
            pstmt.setDate(7, taxRate.getEndDate() != null ? Date.valueOf(taxRate.getEndDate()) : null);
            pstmt.setBoolean(8, taxRate.getIsActive());
            pstmt.setString(9, taxRate.getDescription());
            pstmt.setLong(10, taxRate.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(TaxRate taxRate) {
        String query = "DELETE FROM tax_rates WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, taxRate.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double getTotalTaxRate(String eventType) {
        String query = "SELECT COALESCE(SUM(rate), 0) as total_rate FROM tax_rates " +
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

    private TaxRate mapResultSetToTaxRate(ResultSet rs) throws SQLException {
        TaxRate taxRate = new TaxRate();
        
        taxRate.setId(rs.getLong("id"));
        taxRate.setName(rs.getString("name"));
        taxRate.setRate(rs.getDouble("rate"));
        taxRate.setType(rs.getString("type"));
        taxRate.setApplicability(rs.getString("applicability"));
        taxRate.setEventType(rs.getString("event_type"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            taxRate.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            taxRate.setEndDate(endDate.toLocalDate());
        }
        
        taxRate.setIsActive(rs.getBoolean("is_active"));
        taxRate.setDescription(rs.getString("description"));
        
        return taxRate;
    }
}
