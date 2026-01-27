-- Database schema for SynapseEvent application
-- MySQL database: synapse_event

-- Drop tables if they exist to allow re-initialization
DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS UserPreferences;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS CustomEventRequest;
DROP TABLE IF EXISTS EventTemplate;
DROP TABLE IF EXISTS AnniversaryEvent;
DROP TABLE IF EXISTS FormationEvent;
DROP TABLE IF EXISTS PaddleEvent;
DROP TABLE IF EXISTS PartyingEvent;
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
    description TEXT,
    status VARCHAR(20) DEFAULT 'draft'
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
    created_date DATE NOT NULL
);

-- Insert sample data for Role
INSERT INTO Role (name) VALUES ('Admin'), ('User'), ('Manager');

-- Insert sample data for Enterprise
INSERT INTO Enterprise (nom, siret) VALUES
('TechCorp', '12345678901234'),
('Innovate Ltd', '56789012345678'),
('Global Solutions', '90123456789012');

-- Insert sample data for Utilisateur
-- Passwords are plain text for simplicity, default password is 'password123'
INSERT INTO Utilisateur (email, password, nom, prenom, phone, address, profile_picture, role_id, enterprise_id) VALUES
('admin@techcorp.com', 'password123', 'Dupont', 'Jean', '0123456789', '123 Tech Street, Paris', NULL, 1, 1),
('user@innovate.com', 'password123', 'Martin', 'Marie', '0987654321', '456 Innovate Ave, Lyon', NULL, 2, 2),
('manager@global.com', 'password123', 'Durand', 'Pierre', '0555123456', '789 Global Blvd, Marseille', NULL, 3, 3);

-- Insert sample data for AnniversaryEvent
INSERT INTO AnniversaryEvent (name, date, start_time, end_time, location, capacity, price, organizer, category, description, status) VALUES
('Anniversaire 10 ans TechCorp', '2024-05-15', '10:00:00', '18:00:00', 'Salle de fête TechCorp', 100, 0.00, 'Jean Dupont', 'Corporate', 'Célébration des 10 ans de l\'entreprise avec gâteau et discours', 'published'),
('Anniversaire 5 ans Innovate', '2024-07-20', '14:00:00', '20:00:00', 'Parc Innovate', 50, 25.00, 'Marie Martin', 'Team Building', 'Fête d\'anniversaire avec activités ludiques', 'published'),
('Anniversaire 20 ans Global', '2024-09-10', '09:00:00', '22:00:00', 'Hôtel Global', 200, 50.00, 'Pierre Durand', 'Corporate', 'Grande célébration avec invités externes', 'published'),
('Anniversaire 1 an Startup', '2024-11-05', '16:00:00', '19:00:00', 'Café Startup', 20, 10.00, 'Alice Nouveau', 'Casual', 'Petite fête pour le premier anniversaire', 'published'),
('Anniversaire 15 ans Entreprise', '2024-12-18', '11:00:00', '17:00:00', 'Salle des fêtes Municipale', 150, 30.00, 'Bob Ancien', 'Corporate', 'Événement spécial avec remise de prix', 'published');

-- Insert sample data for FormationEvent
INSERT INTO FormationEvent (name, date, description) VALUES
('Formation Java Avancé', '2024-06-10', 'Cours intensif sur les concepts avancés de Java'),
('Atelier Management', '2024-08-15', 'Formation sur les techniques de management d\'équipe'),
('Séminaire Sécurité Informatique', '2024-10-22', 'Apprendre les bonnes pratiques de sécurité'),
('Workshop Agile', '2024-11-30', 'Introduction aux méthodologies agiles'),
('Formation Leadership', '2025-01-25', 'Développer ses compétences en leadership'),
('Cours Anglais Professionnel', '2025-03-12', 'Améliorer son anglais pour le business');

-- Insert sample data for PaddleEvent
INSERT INTO PaddleEvent (name, date, description) VALUES
('Tournoi Paddle Entreprise', '2024-07-05', 'Compétition amicale entre collègues'),
('Sortie Paddle Été', '2024-08-20', 'Après-midi détente sur le court'),
('Challenge Paddle Teams', '2024-09-14', 'Événement par équipes pour renforcer la cohésion'),
('Initiation Paddle', '2024-10-08', 'Cours pour débutants'),
('Match Paddle Inter-Entreprises', '2024-11-19', 'Rencontre avec une autre société'),
('Soirée Paddle', '2025-01-10', 'Session nocturne pour les amateurs');

-- Insert sample data for PartyingEvent
INSERT INTO PartyingEvent (name, date, description) VALUES
('Soirée d\'Entreprise TechCorp', '2024-06-25', 'Fête avec DJ et buffet'),
('Gala Annuel Innovate', '2024-09-05', 'Événement formel avec cocktail'),
('Fête de Noël Global', '2024-12-20', 'Célébration de fin d\'année'),
('Barbecue d\'Été', '2024-07-30', 'Repas en extérieur avec musique'),
('Soirée Casino', '2024-11-15', 'Jeux et divertissement'),
('Concert Privé', '2025-02-28', 'Performance live pour les employés');

-- Insert sample data for TeamBuildingEvent
INSERT INTO TeamBuildingEvent (name, date, description) VALUES
('Escape Game Team', '2024-08-12', 'Aventure en équipe pour résoudre énigmes'),
('Randonnée Cohésion', '2024-09-28', 'Marche en nature pour renforcer les liens'),
('Atelier Créativité', '2024-10-15', 'Sessions pour stimuler l\'innovation'),
('Challenge Sportif', '2024-11-22', 'Compétitions sportives collectives'),
('Séminaire Motivation', '2025-01-18', 'Conférences pour booster le moral'),
('Voyage d\'Équipe', '2025-03-05', 'Week-end hors site pour l\'équipe');

-- Insert sample data for Booking
INSERT INTO Booking (user_id, event_type, event_id, booking_date, status) VALUES
(1, 'FormationEvent', 1, '2024-06-01', 'confirmed'),
(2, 'PaddleEvent', 1, '2024-07-01', 'pending'),
(3, 'PartyingEvent', 1, '2024-06-20', 'confirmed');

-- Insert sample data for CustomEventRequest
INSERT INTO CustomEventRequest (user_id, event_type, event_date, description, status, created_date) VALUES
(1, 'TeamBuilding', '2024-06-01', 'Request for a custom team building event with outdoor activities', 'pending', '2024-05-01'),
(2, 'Anniversary', '2024-05-01', 'Need a special anniversary celebration with fireworks', 'approved', '2024-04-15'),
(3, 'Formation', '2024-07-01', 'Custom formation on advanced AI topics', 'pending', '2024-05-10');

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

-- Insert sample data for Review
INSERT INTO Review (user_id, event_type, event_id, rating, comment, created_at) VALUES
(2, 'AnniversaryEvent', 1, 5, 'Excellent event, highly recommended!', '2024-05-16 10:00:00'),
(2, 'AnniversaryEvent', 2, 4, 'Good event, but could be better organized.', '2024-07-21 14:00:00');

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

-- Insert sample data for UserPreferences
INSERT INTO UserPreferences (user_id, preferred_categories, preferred_locations, max_price, min_rating) VALUES
(2, 'Corporate,Team Building', 'Paris,Lyon', 50.00, 4);

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

-- Insert sample data for EventTemplate
INSERT INTO EventTemplate (name, event_type, default_start_time, default_end_time, default_capacity, default_price, default_category, default_description, template_description) VALUES
('Standard Anniversary', 'AnniversaryEvent', '10:00:00', '18:00:00', 100, 0.00, 'Corporate', 'Standard anniversary celebration template', 'Template for corporate anniversary events'),
('Team Building Workshop', 'TeamBuildingEvent', '09:00:00', '17:00:00', 20, 150.00, 'Team Building', 'Interactive team building activities', 'Template for team building workshops');

-- Insert sample data for event_instance
INSERT INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES
('Formation Java Avancé', '2024-06-10', NULL, NULL, NULL, NULL, NULL, NULL, 'Cours intensif sur les concepts avancés de Java', 'published', 'Formation'),
('Atelier Management', '2024-08-15', NULL, NULL, NULL, NULL, NULL, NULL, 'Formation sur les techniques de management d\'équipe', 'published', 'Formation'),
('Tournoi Paddle Entreprise', '2024-07-05', NULL, NULL, NULL, NULL, NULL, NULL, 'Compétition amicale entre collègues', 'published', 'Paddle'),
('Soirée d\'Entreprise TechCorp', '2024-06-25', NULL, NULL, NULL, NULL, NULL, NULL, 'Fête avec DJ et buffet', 'published', 'Partying'),
('Escape Game Team', '2024-08-12', NULL, NULL, NULL, NULL, NULL, NULL, 'Aventure en équipe pour résoudre énigmes', 'published', 'TeamBuilding'),
('Anniversaire 10 ans TechCorp', '2024-05-15', '10:00:00', '18:00:00', 'Salle de fête TechCorp', 100, 0.00, 'Jean Dupont', 'Célébration des 10 ans de l\'entreprise avec gâteau et discours', 'published', 'Anniversary');
