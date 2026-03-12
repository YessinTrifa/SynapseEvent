package com.synapseevent.service;

import com.synapseevent.entities.Coupon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponService {
    private Connection conn() {
        return com.synapseevent.utils.MaConnection.getInstance().getConnection();
    }

    public List<Coupon> readAll() {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM coupons ORDER BY created_at DESC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Coupon coupon = mapResultSetToCoupon(rs);
                coupons.add(coupon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return coupons;
    }

    public Coupon findByCode(String code) {
        String query = "SELECT * FROM coupons WHERE code = ? AND is_active = true";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, code.toUpperCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean ajouter(Coupon coupon) {
        String query = "INSERT INTO coupons (code, type, value, start_date, end_date, " +
                     "usage_limit, min_spend, is_active, applicable_event_types, " +
                     "description, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, coupon.getCode().toUpperCase());
            pstmt.setString(2, coupon.getType());
            pstmt.setDouble(3, coupon.getValue());
            pstmt.setDate(4, coupon.getStartDate() != null ? Date.valueOf(coupon.getStartDate()) : null);
            pstmt.setDate(5, coupon.getEndDate() != null ? Date.valueOf(coupon.getEndDate()) : null);
            pstmt.setInt(6, coupon.getUsageLimit());
            pstmt.setDouble(7, coupon.getMinSpend());
            pstmt.setBoolean(8, coupon.getIsActive());
            pstmt.setString(9, coupon.getApplicableEventTypes());
            pstmt.setString(10, coupon.getDescription());
            pstmt.setTimestamp(11, Timestamp.valueOf(coupon.getCreatedAt()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(Coupon coupon) {
        String query = "UPDATE coupons SET type = ?, value = ?, start_date = ?, " +
                     "end_date = ?, usage_limit = ?, min_spend = ?, is_active = ?, " +
                     "applicable_event_types = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, coupon.getType());
            pstmt.setDouble(2, coupon.getValue());
            pstmt.setDate(3, coupon.getStartDate() != null ? Date.valueOf(coupon.getStartDate()) : null);
            pstmt.setDate(4, coupon.getEndDate() != null ? Date.valueOf(coupon.getEndDate()) : null);
            pstmt.setInt(5, coupon.getUsageLimit());
            pstmt.setDouble(6, coupon.getMinSpend());
            pstmt.setBoolean(7, coupon.getIsActive());
            pstmt.setString(8, coupon.getApplicableEventTypes());
            pstmt.setString(9, coupon.getDescription());
            pstmt.setLong(10, coupon.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(Coupon coupon) {
        String query = "DELETE FROM coupons WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, coupon.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markCouponAsUsed(String code) {
        String query = "UPDATE coupons SET used_count = used_count + 1 WHERE code = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, code.toUpperCase());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Coupon> findActiveCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM coupons WHERE is_active = true AND " +
                     "(end_date IS NULL OR end_date >= CURDATE()) ORDER BY created_at DESC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Coupon coupon = mapResultSetToCoupon(rs);
                if (coupon.isValid()) {
                    coupons.add(coupon);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return coupons;
    }

    public List<Coupon> findByEventType(String eventType) {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM coupons WHERE is_active = 1 AND " +
                     "(applicable_event_types IS NULL OR applicable_event_types LIKE ?) " +
                     "ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, "%" + eventType + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Coupon coupon = mapResultSetToCoupon(rs);
                    if (coupon.isValid()) {
                        coupons.add(coupon);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return coupons;
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        
        coupon.setId(rs.getLong("id"));
        coupon.setCode(rs.getString("code"));
        coupon.setType(rs.getString("type"));
        coupon.setValue(rs.getDouble("value"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            coupon.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            coupon.setEndDate(endDate.toLocalDate());
        }
        
        coupon.setUsageLimit(rs.getInt("usage_limit"));
        coupon.setUsedCount(rs.getInt("used_count"));
        coupon.setMinSpend(rs.getDouble("min_spend"));
        coupon.setIsActive(rs.getBoolean("is_active"));
        coupon.setApplicableEventTypes(rs.getString("applicable_event_types"));
        coupon.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            coupon.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return coupon;
    }
}
