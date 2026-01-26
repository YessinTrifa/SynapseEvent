-- Database schema for SynapseEvent application
-- MySQL database: synapse_event

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
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
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
INSERT INTO Utilisateur (email, nom, prenom, role_id, enterprise_id) VALUES
('admin@techcorp.com', 'Dupont', 'Jean', 1, 1),
('user@innovate.com', 'Martin', 'Marie', 2, 2),
('manager@global.com', 'Durand', 'Pierre', 3, 3);

-- Insert sample data for AnniversaryEvent
INSERT INTO AnniversaryEvent (name, date, description) VALUES
('Anniversaire 10 ans TechCorp', '2024-05-15', 'Célébration des 10 ans de l\'entreprise avec gâteau et discours'),
('Anniversaire 5 ans Innovate', '2024-07-20', 'Fête d\'anniversaire avec activités ludiques'),
('Anniversaire 20 ans Global', '2024-09-10', 'Grande célébration avec invités externes'),
('Anniversaire 1 an Startup', '2024-11-05', 'Petite fête pour le premier anniversaire'),
('Anniversaire 15 ans Entreprise', '2024-12-18', 'Événement spécial avec remise de prix'),
('Anniversaire 3 ans Société', '2025-02-14', 'Célébration romantique pour la Saint-Valentin');

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
INSERT INTO CustomEventRequest (user_id, request_details, status, created_date) VALUES
(1, 'Request for a custom team building event with outdoor activities', 'pending', '2024-05-01'),
(2, 'Need a special anniversary celebration with fireworks', 'approved', '2024-04-15'),
(3, 'Custom formation on advanced AI topics', 'pending', '2024-05-10');