-- Database schema for SynapseEvent application
-- MySQL database: synapse_event

-- Drop tables if they exist to allow re-initialization
DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS UserPreferences;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS CustomEventRequest;
DROP TABLE IF EXISTS EventTemplate;
DROP TABLE IF EXISTS PartyingEvent;
DROP TABLE IF EXISTS AnniversaryEvent;
DROP TABLE IF EXISTS FormationEvent;
DROP TABLE IF EXISTS PaddleEvent;
DROP TABLE IF EXISTS Venue;
DROP TABLE IF EXISTS TeamBuildingEvent;
DROP TABLE IF EXISTS event_instance;
DROP TABLE IF EXISTS Utilisateur;
DROP TABLE IF EXISTS Enterprise;
DROP TABLE IF EXISTS Role;

-- Create Role table
CREATE TABLE IF NOT EXISTS Role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create Enterprise table
CREATE TABLE IF NOT EXISTS Enterprise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    siret VARCHAR(255) NOT NULL
);

-- Create Utilisateur table
CREATE TABLE IF NOT EXISTS Utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    profile_picture VARCHAR(500),
    role_id BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES Role(id) ON DELETE CASCADE,
    FOREIGN KEY (enterprise_id) REFERENCES Enterprise(id) ON DELETE CASCADE
);

-- Create venue table (for party locations: clubs, beaches, hotels)
CREATE TABLE IF NOT EXISTS Venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'CLUB, BEACH, or HOTEL',
    address VARCHAR(500),
    contact_info VARCHAR(255),
    price_range VARCHAR(10) COMMENT '€, €€, €€€',
    rating DECIMAL(2,1) DEFAULT 0,
    description TEXT,
    amenities TEXT
);

-- Create AnniversaryEvent table
CREATE TABLE IF NOT EXISTS AnniversaryEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255),
    capacity INT,
    price DECIMAL(10,2),
    organizer VARCHAR(255),
    category VARCHAR(100),
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft'
);

-- Create FormationEvent table
CREATE TABLE IF NOT EXISTS FormationEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft'
);

-- Create PaddleEvent table
CREATE TABLE IF NOT EXISTS PaddleEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft'
);

-- Create PartyingEvent table
CREATE TABLE IF NOT EXISTS PartyingEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    venue_id BIGINT,
    capacity INT,
    price DECIMAL(10,2),
    organizer VARCHAR(255),
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft',
    FOREIGN KEY (venue_id) REFERENCES Venue(id) ON DELETE SET NULL
);

-- Create TeamBuildingEvent table
CREATE TABLE IF NOT EXISTS TeamBuildingEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft'
);

-- Create Booking table
CREATE TABLE IF NOT EXISTS Booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Create CustomEventRequest table
CREATE TABLE IF NOT EXISTS CustomEventRequest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    created_date DATE NOT NULL,
    budget DECIMAL(10,2),
    capacity INT,
    location VARCHAR(255),
    reason TEXT
);

-- Create Review table
CREATE TABLE IF NOT EXISTS Review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Create UserPreferences table
CREATE TABLE IF NOT EXISTS UserPreferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    preferred_categories VARCHAR(500),
    preferred_locations VARCHAR(500),
    max_price DECIMAL(10,2),
    min_rating INT,
    FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Create EventTemplate table
CREATE TABLE IF NOT EXISTS EventTemplate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    default_start_time TIME,
    default_end_time TIME,
    default_capacity INT,
    default_price DECIMAL(10,2),
    default_category VARCHAR(100),
    default_description TEXT,
    template_description TEXT
);

-- Create event_instance table
CREATE TABLE IF NOT EXISTS event_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255),
    capacity INT,
    price DECIMAL(10,2),
    organizer VARCHAR(255),
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft',
    type VARCHAR(50) NOT NULL
);
