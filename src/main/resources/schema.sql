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
DROP TABLE IF EXISTS CustomEventType;

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
    city VARCHAR(255),
    contact_info VARCHAR(255),
    price_range VARCHAR(10) COMMENT 'TND',
    rating DECIMAL(2,1) DEFAULT 0,
    description TEXT,
    amenities TEXT
);
CREATE TABLE IF NOT EXISTS CustomEventType (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    date DATE,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255),
    capacity INT,
    price DOUBLE,
    organizer VARCHAR(255),
    description TEXT,
    status VARCHAR(50)
    );

-- Create PaddleEvent table
CREATE TABLE IF NOT EXISTS PaddleEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255),
    map VARCHAR(500),
    capacity INT,
    reservation INT DEFAULT 0,
    price DECIMAL(10,2),
    disponibilite BOOLEAN DEFAULT TRUE,
    organizer VARCHAR(255),
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
    theme VARCHAR(100),
    music_type VARCHAR(100),
    age_restriction INT DEFAULT 18,
    FOREIGN KEY (venue_id) REFERENCES Venue(id) ON DELETE SET NULL
);

-- Create TeamBuildingEvent table
CREATE TABLE IF NOT EXISTS TeamBuildingEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE,
    start_time TIME,
    end_time TIME,
    location VARCHAR(255),
    capacity INT,
    price DOUBLE,
    organizer VARCHAR(255),
    description TEXT,
    status VARCHAR(50)
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

-- Create reservations table for the new reservation system
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    seats INT NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    event_type VARCHAR(20) DEFAULT 'PADDLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Insert sample data for testing
INSERT IGNORE INTO Role (id, name) VALUES (1, 'Admin'), (2, 'User');
INSERT IGNORE INTO Enterprise (id, nom, siret) VALUES (1, 'SynapseEvent', '123456789');
INSERT IGNORE INTO Utilisateur (id, email, password, nom, prenom, role_id, enterprise_id) 
VALUES (1, 'admin@synapse.com', '$2a$10$YourHashedPasswordHere', 'Admin', 'User', 1, 1);

INSERT IGNORE INTO Venue (id, name, type, address, city) 
VALUES (1, 'Paddle Club Tunis', 'CLUB', 'Avenue Habib Bourguiba', 'Tunis'),
       (2, 'Beach Padel Sousse', 'BEACH', 'Bord de mer', 'Sousse');

INSERT IGNORE INTO PaddleEvent (id, name, date, start_time, end_time, location, capacity, price, status, description) 
VALUES (1, 'Tournament Padel Elite', '2026-03-15', '09:00:00', '17:00:00', 'Paddle Club Tunis', 20, 50.00, 'published', 'Elite tournament for advanced players'),
       (2, 'Paddle Initiation', '2026-03-20', '14:00:00', '16:00:00', 'Beach Padel Sousse', 15, 30.00, 'published', 'Perfect introduction to paddle for beginners'),
       (3, 'Beach Paddle Party', '2026-03-25', '10:00:00', '18:00:00', 'Beach Paddle Sousse', 25, 40.00, 'published', 'Fun beach paddle day with music and food');
 
 