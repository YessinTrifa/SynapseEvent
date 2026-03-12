-- Pricing Engine Database Schema for SynapseEvent
-- This file contains the database schema for the new pricing system

-- Coupons Table
CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    type ENUM('PERCENT', 'FIXED') NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    start_date DATE,
    end_date DATE,
    usage_limit INT,
    used_count INT DEFAULT 0,
    min_spend DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    applicable_event_types TEXT, -- Comma-separated list of event types
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_active_dates (is_active, start_date, end_date),
    INDEX idx_created_at (created_at)
);

-- Pricing Rules Table
CREATE TABLE IF NOT EXISTS pricing_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50), -- NULL applies to all event types
    rule_type ENUM('VOLUME', 'EARLY_BIRD', 'OFF_PEAK', 'GROUP_SIZE', 'VENUE_BASE') NOT NULL,
    condition_type ENUM('MIN_PEOPLE', 'EXACT_PEOPLE', 'RANGE_MIN', 'DAYS_BEFORE_EVENT', 'DAY_OF_WEEK') NOT NULL,
    condition_value DECIMAL(10,2) NOT NULL,
    discount_type ENUM('PERCENT', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_rule (event_type, rule_type),
    INDEX idx_active_dates (is_active, start_date, end_date)
);

-- Payment Schedules Table
CREATE TABLE IF NOT EXISTS payment_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    installment_number INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE,
    status ENUM('PENDING', 'PAID', 'OVERDUE', 'CANCELLED') DEFAULT 'PENDING',
    payment_method VARCHAR(100),
    description TEXT,
    is_deposit BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reminder_sent TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_booking_id (booking_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    INDEX idx_overdue (status, due_date)
);

-- Tax Rates Table
CREATE TABLE IF NOT EXISTS tax_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    rate DECIMAL(5,2) NOT NULL,
    type ENUM('PERCENTAGE', 'FIXED_AMOUNT') DEFAULT 'PERCENTAGE',
    applicability ENUM('ALL', 'VENUE', 'ACTIVITY', 'SERVICE') DEFAULT 'ALL',
    event_type VARCHAR(50), -- NULL applies to all event types
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_type (event_type),
    INDEX idx_active_dates (is_active, start_date, end_date)
);

-- Service Fee Rates Table
CREATE TABLE IF NOT EXISTS service_fee_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    rate DECIMAL(5,2) NOT NULL,
    type ENUM('PERCENTAGE', 'FIXED_AMOUNT') DEFAULT 'PERCENTAGE',
    applicability ENUM('ALL', 'VENUE_ONLY', 'ACTIVITY_ONLY') DEFAULT 'ALL',
    event_type VARCHAR(50), -- NULL applies to all event types
    min_amount DECIMAL(10,2),
    max_amount DECIMAL(10,2),
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_type (event_type),
    INDEX idx_active_dates (is_active, start_date, end_date)
);

-- Update existing venues table to support pricing
ALTER TABLE venues 
ADD COLUMN IF NOT EXISTS base_fee DECIMAL(10,2),
ADD COLUMN IF NOT EXISTS per_person_fee DECIMAL(10,2),
ADD COLUMN IF NOT EXISTS capacity INT,
ADD COLUMN IF NOT EXISTS has_pricing_rules BOOLEAN DEFAULT FALSE;

-- Update existing team_building_activities table to support pricing
ALTER TABLE team_building_activities 
ADD COLUMN IF NOT EXISTS min_participants INT DEFAULT 1,
ADD COLUMN IF NOT EXISTS max_participants INT DEFAULT 100;

-- Insert default tax rates
INSERT IGNORE INTO tax_rates (name, rate, type, applicability, description) VALUES
('VAT', 19.0, 'PERCENTAGE', 'ALL', 'Standard VAT rate'),
('Service Tax', 5.0, 'PERCENTAGE', 'SERVICE', 'Service tax on additional services'),
('Event Tax', 2.0, 'PERCENTAGE', 'ACTIVITY', 'Tax on activities');

-- Insert default service fee rates
INSERT IGNORE INTO service_fee_rates (name, rate, type, applicability, description) VALUES
('Processing Fee', 2.5, 'PERCENTAGE', 'ALL', 'Standard processing fee'),
('Platform Fee', 1.0, 'PERCENTAGE', 'ALL', 'Platform usage fee'),
('Payment Processing', 0.5, 'PERCENTAGE', 'ALL', 'Payment gateway fee');

-- Insert sample pricing rules
INSERT IGNORE INTO pricing_rules (event_type, rule_type, condition_type, condition_value, discount_type, discount_value, description) VALUES
('TeamBuilding', 'GROUP_SIZE', 'MIN_PEOPLE', 10, 'PERCENT', 10.0, '10% off for groups of 10 or more'),
('TeamBuilding', 'GROUP_SIZE', 'MIN_PEOPLE', 25, 'PERCENT', 15.0, '15% off for groups of 25 or more'),
('TeamBuilding', 'EARLY_BIRD', 'DAYS_BEFORE_EVENT', 30, 'PERCENT', 20.0, '20% off for bookings 30+ days before event'),
('TeamBuilding', 'EARLY_BIRD', 'DAYS_BEFORE_EVENT', 14, 'PERCENT', 10.0, '10% off for bookings 14+ days before event'),
('ALL', 'OFF_PEAK', 'DAY_OF_WEEK', 2, 'PERCENT', 5.0, '5% off for Monday bookings'),
('ALL', 'OFF_PEAK', 'DAY_OF_WEEK', 3, 'PERCENT', 5.0, '5% off for Tuesday bookings'),
('ALL', 'OFF_PEAK', 'DAY_OF_WEEK', 4, 'PERCENT', 5.0, '5% off for Wednesday bookings');

-- Insert sample coupons
INSERT IGNORE INTO coupons (code, type, value, start_date, end_date, usage_limit, min_spend, applicable_event_types, description) VALUES
('WELCOME10', 'PERCENT', 10.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 100, 50.0, NULL, 'Welcome discount for new customers'),
('TEAM2024', 'FIXED', 25.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 50, 100.0, 'TeamBuilding', 'Team building special offer'),
('EARLYBIRD', 'PERCENT', 15.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 200, 200.0, NULL, 'Early bird special discount'),
('CORPORATE20', 'PERCENT', 20.0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 100, 500.0, 'TeamBuilding,Anniversary', 'Corporate client discount');

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_coupons_active ON coupons(is_active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_pricing_rules_active ON pricing_rules(is_active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_tax_rates_active ON tax_rates(is_active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_service_fees_active ON service_fee_rates(is_active, start_date, end_date);
