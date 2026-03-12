package com.synapseevent.service;

import com.synapseevent.entities.PricingRules;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PricingRulesService {
    private Connection conn() {
        return com.synapseevent.utils.MaConnection.getInstance().getConnection();
    }

    public List<PricingRules> readAll() {
        List<PricingRules> rules = new ArrayList<>();
        String query = "SELECT * FROM pricing_rules WHERE is_active = true ORDER BY created_at DESC";
        
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PricingRules rule = mapResultSetToPricingRule(rs);
                rules.add(rule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rules;
    }

    public PricingRules findById(Long id) {
        String query = "SELECT * FROM pricing_rules WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricingRule(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<PricingRules> findByEventType(String eventType) {
        List<PricingRules> rules = new ArrayList<>();
        String query = "SELECT * FROM pricing_rules WHERE is_active = true AND " +
                     "(event_type = ? OR event_type = 'ALL' OR event_type IS NULL) " +
                     "ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, eventType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PricingRules rule = mapResultSetToPricingRule(rs);
                    if (rule.appliesToEvent(eventType)) {
                        rules.add(rule);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rules;
    }

    public List<PricingRules> findByEventTypeAndRuleType(String eventType, String ruleType) {
        List<PricingRules> rules = new ArrayList<>();
        String query = "SELECT * FROM pricing_rules WHERE is_active = true AND " +
                     "rule_type = ? AND (event_type = ? OR event_type = 'ALL' OR event_type IS NULL) " +
                     "ORDER BY condition_value DESC";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, ruleType);
            pstmt.setString(2, eventType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PricingRules rule = mapResultSetToPricingRule(rs);
                    if (rule.appliesToEvent(eventType)) {
                        rules.add(rule);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rules;
    }

    public List<PricingRules> findVolumeDiscounts(String eventType) {
        return findByEventTypeAndRuleType(eventType, "GROUP_SIZE");
    }

    public List<PricingRules> findEarlyBirdDiscounts(String eventType) {
        return findByEventTypeAndRuleType(eventType, "EARLY_BIRD");
    }

    public List<PricingRules> findOffPeakDiscounts(String eventType) {
        return findByEventTypeAndRuleType(eventType, "OFF_PEAK");
    }

    public boolean ajouter(PricingRules rule) {
        String query = "INSERT INTO pricing_rules (event_type, rule_type, condition_type, " +
                     "condition_value, discount_type, discount_value, start_date, " +
                     "end_date, is_active, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, rule.getEventType());
            pstmt.setString(2, rule.getRuleType());
            pstmt.setString(3, rule.getConditionType());
            pstmt.setDouble(4, rule.getConditionValue());
            pstmt.setString(5, rule.getDiscountType());
            pstmt.setDouble(6, rule.getDiscountValue());
            pstmt.setDate(7, rule.getStartDate() != null ? Date.valueOf(rule.getStartDate()) : null);
            pstmt.setDate(8, rule.getEndDate() != null ? Date.valueOf(rule.getEndDate()) : null);
            pstmt.setBoolean(9, rule.getIsActive());
            pstmt.setString(10, rule.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(PricingRules rule) {
        String query = "UPDATE pricing_rules SET event_type = ?, rule_type = ?, " +
                     "condition_type = ?, condition_value = ?, discount_type = ?, " +
                     "discount_value = ?, start_date = ?, end_date = ?, " +
                     "is_active = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setString(1, rule.getEventType());
            pstmt.setString(2, rule.getRuleType());
            pstmt.setString(3, rule.getConditionType());
            pstmt.setDouble(4, rule.getConditionValue());
            pstmt.setString(5, rule.getDiscountType());
            pstmt.setDouble(6, rule.getDiscountValue());
            pstmt.setDate(7, rule.getStartDate() != null ? Date.valueOf(rule.getStartDate()) : null);
            pstmt.setDate(8, rule.getEndDate() != null ? Date.valueOf(rule.getEndDate()) : null);
            pstmt.setBoolean(9, rule.getIsActive());
            pstmt.setString(10, rule.getDescription());
            pstmt.setLong(11, rule.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(PricingRules rule) {
        String query = "DELETE FROM pricing_rules WHERE id = ?";
        
        try (PreparedStatement pstmt = conn().prepareStatement(query)) {
            pstmt.setLong(1, rule.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PricingRules mapResultSetToPricingRule(ResultSet rs) throws SQLException {
        PricingRules rule = new PricingRules();
        
        rule.setId(rs.getLong("id"));
        rule.setEventType(rs.getString("event_type"));
        rule.setRuleType(rs.getString("rule_type"));
        rule.setConditionType(rs.getString("condition_type"));
        rule.setConditionValue(rs.getDouble("condition_value"));
        rule.setDiscountType(rs.getString("discount_type"));
        rule.setDiscountValue(rs.getDouble("discount_value"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            rule.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            rule.setEndDate(endDate.toLocalDate());
        }
        
        rule.setIsActive(rs.getBoolean("is_active"));
        rule.setDescription(rs.getString("description"));
        
        return rule;
    }
}
