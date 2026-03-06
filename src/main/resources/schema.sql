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

-- Create venue table (for party locations: clubs, beaches, hotels, bars, restaurants)
CREATE TABLE IF NOT EXISTS Venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'CLUB, BEACH, HOTEL, BAR, or RESTAURANT',
    address VARCHAR(500),
    city VARCHAR(255),
    contact_info VARCHAR(255),
    price_range VARCHAR(50) COMMENT 'TND',
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
    status VARCHAR(50),
    is_pack BOOLEAN DEFAULT FALSE,
    activities TEXT
    );

-- Create TeamBuildingActivity table for games and activities
CREATE TABLE IF NOT EXISTS TeamBuildingActivity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    duration_minutes INT,
    price_per_person DECIMAL(10,2),
    min_participants INT DEFAULT 1,
    max_participants INT DEFAULT 100,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create linking table for TeamBuildingEvent and activities (for packs)
CREATE TABLE IF NOT EXISTS TeamBuildingEventActivity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES TeamBuildingEvent(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES TeamBuildingActivity(id) ON DELETE CASCADE
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

-- Create Court table for paddle court reservations
CREATE TABLE IF NOT EXISTS Court (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    venue_id BIGINT NOT NULL,
    is_indoor BOOLEAN DEFAULT FALSE,
    price_per_hour DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    description TEXT,
    amenities TEXT,
    FOREIGN KEY (venue_id) REFERENCES Venue(id) ON DELETE CASCADE
);

-- Create court_reservations table for court bookings
CREATE TABLE IF NOT EXISTS court_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    court_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (court_id) REFERENCES Court(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Insert sample data for testing
INSERT IGNORE INTO Role (id, name) VALUES (1, 'Admin'), (2, 'User');
INSERT IGNORE INTO Enterprise (id, nom, siret) VALUES (1, 'SynapseEvent', '123456789');
INSERT IGNORE INTO Utilisateur (id, email, password, nom, prenom, role_id, enterprise_id) 
VALUES (1, 'admin@synapse.com', '$2a$10$YourHashedPasswordHere', 'Admin', 'User', 1, 1);

INSERT IGNORE INTO Venue (id, name, type, address, city, contact_info, price_range, rating, description, amenities) VALUES
(1, 'Rooftop Bellezza Event', 'HOTEL', 'Tunis', 'Tunis', 'Contact via venue', '1500-3500', 4.5, 'Birthday, Anniversary, Reception venue with rooftop setting', 'Rooftop access, Catering options, Sound system'),
(2, 'Salle Blanche', 'HOTEL', 'Tunis', 'Tunis', 'Contact via venue', '3000-7000', 4.8, 'Weddings, Large parties, Receptions - elegant ballroom', 'Ballroom, Catering, Valet parking, DJ booth'),
(3, 'Salle des fêteS Queen', 'HOTEL', 'Tunis', 'Tunis', 'Contact via venue', '4000-10000', 4.7, 'Weddings, Big celebrations - capacity ~350 guests', 'Large hall, Stage, Kitchen, Parking, Air conditioning'),
(4, 'Massaya Odéon', 'HOTEL', 'Gammarth, Tunis', 'Tunis', 'Contact via venue', '3000-8000', 4.3, 'Weddings, Parties venue', 'Garden, Indoor space, Catering available'),
(5, 'La Perle du Lac', 'HOTEL', 'Lac 1, Tunis', 'Tunis', 'Contact via venue', '2500-6000', 4.4, 'Parties, Anniversaries by the lake', 'Lake view, Terrace, Catering, Parking'),
(6, 'Espace Dream', 'HOTEL', 'Lac 1, Tunis', 'Tunis', 'Contact via venue', '2000-5000', 4.2, 'Small/medium events, celebrations', 'Modern space, Audio/Video equipment, WiFi'),
(7, 'Le Doyen', 'HOTEL', 'Lac 1, Tunis', 'Tunis', 'Contact via venue', '3000-8000', 4.6, 'Receptions, private events', 'Private venue, Catering, Valet, Security'),
(8, 'Marillot', 'BAR', 'Centre Urbain Nord, Tunis', 'Tunis', 'Contact via venue', '24-600', 4.0, 'Bar, Restaurant, events', 'Bar, Restaurant seating, Event space'),
(9, 'Canette', 'BAR', 'Lafayette, Tunis', 'Tunis', 'Contact via venue', '10-350', 3.8, 'BAR', 'Bar seating, Drinks, Atmosphere'),
(10, 'Follamour', 'RESTAURANT', 'Gammarth, Tunis', 'Tunis', 'Contact via venue', '20-1500', 4.1, 'Lounge, private events', 'Lounge seating, Private rooms, Catering'),
(11, 'Jolene', 'RESTAURANT', 'Gammarth, Tunis', 'Tunis', 'Contact via venue', '30-2300', 4.2, 'Lounge, events', 'Lounge, Event space, Bar, Music system'),
(12, 'Domaine Tarenti', 'HOTEL', 'Near Tunis', 'Tunis', 'Contact via venue', 'Custom', 4.4, 'Team building, Corporate retreats, Workshops, Outdoor activities', 'Outdoor activities, Meeting rooms, Accommodation, Team building equipment'),
(13, 'Meeting & Training Rooms (Spaces/Regus)', 'HOTEL', 'Ben Arous, Mégrine, Hammamet, etc.', 'Tunis', 'Contact via venue', '500-2000', 4.0, 'Trainings, Workshops, Meetings', 'Conference rooms, Projector, WiFi, Whiteboard'),
(14, 'Hotel Conference Rooms', 'HOTEL', 'Tunis/Grand Tunis', 'Tunis', 'Contact via venue', '1000-5000', 4.3, 'Seminars, Trainings, Conferences', 'Conference hall, Catering, A/V equipment, Parking'),
(15, 'Conference & Congress Centres', 'HOTEL', 'Tunis/Grand Tunis', 'Tunis', 'Contact via venue', 'Custom', 4.5, 'Large conferences, seminars, corporate launches', 'Large halls, Multiple rooms, Catering, Interpretation services, Stage'),
(16, 'Landscapes of Cap Bon', 'BEACH', 'Cap Bon Region', 'Nabeul', 'Contact via venue', 'Custom', 4.6, 'Outdoor team building (nature, challenge activities)', 'Nature trails, Team challenges, Outdoor activities, Guide services'),
(17, 'Djerba Explore Centre', 'BEACH', 'Djerba', 'Djerba', 'Contact via venue', 'Custom', 4.7, 'Gala evenings, concerts, workshops, team activities', 'Event spaces, Workshops, Activities, Accommodation nearby'),
(18, 'Arena Paddle Court', 'CLUB', 'Tunis', 'Tunis', 'Contact via venue', 'Custom', 4.3, 'Paddle court for events and training', 'Paddle court, Equipment rental, Lighting'),
(19, 'Paddle Court Marsa', 'CLUB', 'Marsa, Tunis', 'Tunis', 'Contact via venue', 'Custom', 4.4, 'Paddle court for events and training', 'Paddle court, Equipment rental, Changing rooms'),
(20, 'Paddle LAC', 'CLUB', 'Lac, Tunis', 'Tunis', 'Contact via venue', 'Custom', 4.2, 'Paddle court for events and training', 'Paddle court, Equipment rental, Parking');

INSERT IGNORE INTO PaddleEvent (id, name, date, start_time, end_time, location, capacity, price, organizer, description, status, disponibilite) VALUES
(1, 'Tournament Padel Elite', '2026-03-15', '09:00:00', '17:00:00', 'Paddle Club Tunis', 20, 50.00, 'SynapseEvent', 'Elite tournament for advanced players', 'published', TRUE),
(2, 'Paddle Initiation', '2026-03-20', '14:00:00', '16:00:00', 'Beach Padel Sousse', 15, 30.00, 'SynapseEvent', 'Perfect introduction to paddle for beginners', 'published', TRUE),
(3, 'Beach Paddle Party', '2026-03-25', '10:00:00', '18:00:00', 'Beach Paddle Sousse', 25, 40.00, 'SynapseEvent', 'Fun beach paddle day with music and food', 'published', TRUE),
(4, 'Arena Paddle Championship', '2026-04-20', '09:00:00', '18:00:00', 'Arena Paddle Court', 24, 80.00, 'SynapseEvent', 'Competitive paddle tournament for all levels', 'published', TRUE),
(5, 'Morning Paddle Training', '2026-04-18', '07:00:00', '09:00:00', 'Paddle Court Marsa', 12, 40.00, 'SynapseEvent', 'Professional paddle coaching session', 'published', TRUE),
(6, 'Weekend Paddle Social', '2026-04-26', '10:00:00', '14:00:00', 'Paddle LAC', 16, 50.00, 'SynapseEvent', 'Casual paddle game with fellow enthusiasts', 'published', TRUE),
(7, 'Paddle for Beginners', '2026-05-05', '15:00:00', '17:00:00', 'Arena Paddle Court', 10, 30.00, 'SynapseEvent', 'Learn paddle from scratch', 'published', TRUE),
(8, 'Sunset Paddle Session', '2026-05-15', '18:00:00', '20:00:00', 'Paddle Court Marsa', 12, 45.00, 'SynapseEvent', 'Enjoy paddle during sunset', 'published', TRUE),
(9, 'LAC Paddle Open', '2026-06-01', '09:00:00', '17:00:00', 'Paddle LAC', 20, 70.00, 'SynapseEvent', 'Annual paddle open tournament', 'published', TRUE);

-- Insert PartyingEvent data from venues.sql
INSERT IGNORE INTO PartyingEvent (name, date, start_time, end_time, venue_id, capacity, price, organizer, description, status, theme, music_type, age_restriction) VALUES
('Summer Birthday Bash', '2026-04-15', '20:00:00', '02:00:00', 1, 80, 150.00, 'SynapseEvent', 'Celebrate your birthday with stunning rooftop views', 'published', 'Summer Night', 'Mixed', 18),
('Elegant Wedding Reception', '2026-05-20', '18:00:00', '04:00:00', 2, 200, 5000.00, 'SynapseEvent', 'A grand wedding reception in an elegant ballroom', 'published', 'White Wedding', 'Classical & Oriental', 18),
('Golden Anniversary Gala', '2026-06-10', '19:00:00', '23:00:00', 5, 100, 3000.00, 'SynapseEvent', 'Celebrate 50 years of excellence with lake views', 'published', 'Golden Night', 'Jazz & Lounge', 18),
('Royal Wedding Celebration', '2026-07-25', '17:00:00', '05:00:00', 3, 350, 8000.00, 'SynapseEvent', 'A royal wedding celebration for up to 350 guests', 'published', 'Royal', 'Oriental & Western', 18),
('End of Year Corporate Party', '2026-12-31', '20:00:00', '03:00:00', 7, 120, 4500.00, 'SynapseEvent', 'Exclusive corporate year-end celebration', 'published', 'Gala', 'DJ & Live Music', 18),
('Kids Birthday Party', '2026-04-20', '14:00:00', '18:00:00', 6, 30, 500.00, 'SynapseEvent', 'Fun birthday celebration for children', 'published', 'Superheroes', 'Kids Music', 0),
('Engagement Party', '2026-05-05', '19:00:00', '23:00:00', 4, 80, 2500.00, 'SynapseEvent', 'Celebrate your engagement in a beautiful garden setting', 'published', 'Romantic', 'Acoustic & Lounge', 18),
('Jazz Night at Marillot', '2026-04-12', '21:00:00', '02:00:00', 8, 50, 50.00, 'SynapseEvent', 'Enjoy live jazz performances with fine drinks', 'published', 'Jazz Evening', 'Live Jazz', 18),
('80s Retro Night', '2026-04-25', '22:00:00', '03:00:00', 9, 40, 30.00, 'SynapseEvent', 'Travel back to the 80s with retro vibes', 'published', '80s Retro', 'Retro Hits', 18),
('Exclusive Lounge Party', '2026-05-15', '20:00:00', '02:00:00', 10, 60, 200.00, 'SynapseEvent', 'An exclusive evening in a luxury lounge', 'published', 'Elegant', 'Deep House', 18),
('Sunday Brunch Party', '2026-04-27', '12:00:00', '17:00:00', 11, 45, 80.00, 'SynapseEvent', 'Relaxed Sunday brunch with music and drinks', 'published', 'Chillout', 'Acoustic', 18);

-- Insert FormationEvent data from venues.sql
INSERT IGNORE INTO FormationEvent (name, date, start_time, end_time, location, capacity, price, organizer, description, status) VALUES
('Leadership Excellence Workshop', '2026-04-18', '09:00:00', '17:00:00', 'Hotel Conference Rooms', 30, 350.00, 'SynapseEvent', 'Develop leadership skills for managers', 'published'),
('Digital Marketing Masterclass', '2026-05-10', '10:00:00', '16:00:00', 'Meeting & Training Rooms (Spaces/Regus)', 20, 250.00, 'SynapseEvent', 'Learn latest digital marketing strategies', 'published'),
('PMP Certification Prep Course', '2026-06-05', '08:00:00', '18:00:00', 'Conference & Congress Centres', 40, 800.00, 'SynapseEvent', 'Comprehensive project management certification', 'published'),
('Effective Team Building', '2026-04-25', '09:00:00', '17:00:00', 'Domaine Tarenti', 25, 400.00, 'SynapseEvent', 'Interactive workshop to improve team cohesion', 'published'),
('Customer Service Excellence', '2026-05-22', '09:00:00', '13:00:00', 'Hotel Conference Rooms', 25, 150.00, 'SynapseEvent', 'Enhance customer service skills', 'published'),
('Modern HR Practices', '2026-06-15', '10:00:00', '16:00:00', 'Meeting & Training Rooms (Spaces/Regus)', 15, 300.00, 'SynapseEvent', 'Latest trends in human resources management', 'published');

-- Insert TeamBuildingEvent data from venues.sql
INSERT IGNORE INTO TeamBuildingEvent (name, date, start_time, end_time, location, capacity, price, organizer, description, status) VALUES
('Cap Bon Adventure Challenge', '2026-05-02', '08:00:00', '18:00:00', 'Landscapes of Cap Bon', 50, 450.00, 'SynapseEvent', 'Outdoor team building with nature challenges', 'published'),
('Corporate Team Retreat', '2026-06-12', '09:00:00', '17:00:00', 'Domaine Tarenti', 40, 600.00, 'SynapseEvent', 'Team building and strategy sessions', 'published'),
('Djerba Team Experience', '2026-07-10', '09:00:00', '18:00:00', 'Djerba Explore Centre', 60, 550.00, 'SynapseEvent', 'Team activities in Djerba with gala evening', 'published'),
('Outdoor Problem Solving', '2026-05-30', '10:00:00', '16:00:00', 'Landscapes of Cap Bon', 30, 350.00, 'SynapseEvent', 'Team challenges and problem solving exercises', 'published'),
('Leadership in Nature', '2026-06-20', '08:00:00', '17:00:00', 'Domaine Tarenti', 25, 500.00, 'SynapseEvent', 'Leadership development through outdoor activities', 'published');

-- Insert AnniversaryEvent data from venues.sql
INSERT IGNORE INTO AnniversaryEvent (name, date, start_time, end_time, location, capacity, price, organizer, category, description, status) VALUES
('Company 10th Anniversary', '2026-06-20', '19:00:00', '23:00:00', 'Le Doyen', 100, 5000.00, 'SynapseEvent', 'Corporate', 'Celebrate 10 years of success', 'published'),
('Silver Wedding Anniversary', '2026-07-15', '19:00:00', '23:00:00', 'La Perle du Lac', 80, 3500.00, 'SynapseEvent', 'Wedding', 'Celebrate 25 years of marriage', 'published'),
('50th Birthday Celebration', '2026-08-10', '18:00:00', '02:00:00', 'Salle Blanche', 150, 6000.00, 'SynapseEvent', 'Birthday', 'Golden birthday milestone celebration', 'published');

-- Insert event_instance data from venues.sql
INSERT IGNORE INTO event_instance (name, date, start_time, end_time, location, capacity, price, organizer, description, status, type) VALUES
('New Year Eve Gala', '2026-12-31', '20:00:00', '04:00:00', 'Salle des fêteS Queen', 350, 250.00, 'SynapseEvent', 'Grand New Year celebration', 'published', 'PARTY'),
('Summer Pool Party', '2026-07-15', '14:00:00', '22:00:00', 'Massaya Odéon', 100, 100.00, 'SynapseEvent', 'Cool summer pool party', 'published', 'PARTY'),
('Halloween Night', '2026-10-31', '21:00:00', '03:00:00', 'Marillot', 80, 60.00, 'SynapseEvent', 'Spooky Halloween celebration', 'published', 'PARTY'),
('Tech Innovation Summit', '2026-09-15', '09:00:00', '18:00:00', 'Conference & Congress Centres', 200, 150.00, 'SynapseEvent', 'Annual technology innovation conference', 'published', 'CORPORATE'),
('Startup Pitch Day', '2026-06-25', '10:00:00', '17:00:00', 'Hotel Conference Rooms', 50, 0.00, 'SynapseEvent', 'Startup pitch competition', 'published', 'CORPORATE'),
('Paddle Charity Match', '2026-05-20', '10:00:00', '16:00:00', 'Arena Paddle Court', 30, 25.00, 'SynapseEvent', 'Charity paddle tournament', 'published', 'SPORTS'),
('Weekend Beach Party', '2026-04-05', '15:00:00', '23:00:00', 'Costa del Sol', 150, 80.00, 'SynapseEvent', 'Beach party with DJ and drinks', 'published', 'Partying'),
('Neon Night Club Event', '2026-04-12', '22:00:00', '03:00:00', 'The Club', 200, 100.00, 'SynapseEvent', 'Neon themed party night', 'published', 'Partying'),
('Weekend Padel Tournament', '2026-04-20', '09:00:00', '17:00:00', 'Paddle Club Tunis', 24, 45.00, 'SynapseEvent', 'Weekend padel tournament for all levels', 'published', 'Paddle'),
('Paddle Beginner Workshop', '2026-04-25', '10:00:00', '12:00:00', 'Beach Padel Sousse', 12, 25.00, 'SynapseEvent', 'Learn padel basics with professional coaches', 'published', 'Paddle'),
('Corporate Team Building Day', '2026-05-15', '09:00:00', '18:00:00', 'La Villa Hotel', 50, 150.00, 'SynapseEvent', 'Team building activities and workshops', 'published', 'TeamBuilding'),
('Outdoor Adventure Challenge', '2026-06-01', '08:00:00', '17:00:00', 'Sahara Resort', 40, 200.00, 'SynapseEvent', 'Adventure and team bonding in the desert', 'published', 'TeamBuilding'),
('Leadership Skills Workshop', '2026-05-10', '09:00:00', '16:00:00', 'Business Center', 30, 300.00, 'SynapseEvent', 'Professional leadership development course', 'published', 'Formation'),
('Digital Marketing Training', '2026-05-22', '10:00:00', '17:00:00', 'Tech Hub Tunis', 25, 250.00, 'SynapseEvent', 'Learn modern digital marketing strategies', 'published', 'Formation'),
('Company 10th Anniversary Gala', '2026-07-01', '19:00:00', '23:00:00', 'Grand Hotel', 300, 200.00, 'SynapseEvent', 'Celebrating a decade of excellence', 'published', 'Anniversary'),
('Silver Jubilee Celebration', '2026-08-15', '18:00:00', '02:00:00', 'Royal Palace', 200, 500.00, 'SynapseEvent', '25th anniversary celebration', 'published', 'Anniversary');

-- Insert TeamBuildingActivity sample data
INSERT IGNORE INTO TeamBuildingActivity (id, name, description, category, duration_minutes, price_per_person, min_participants, max_participants) VALUES
(1, 'Escape Room Challenge', 'Solve puzzles and escape within time limit', 'Indoor', 60, 25.00, 4, 20),
(2, 'Laser Tag', 'Team-based laser tag combat game', 'Indoor', 90, 30.00, 10, 30),
(3, 'Bowling Tournament', 'Competitive bowling with scoring', 'Indoor', 120, 20.00, 8, 40),
(4, 'Karting Race', 'Go-kart racing competition', 'Outdoor', 60, 35.00, 8, 24),
(5, 'Treasure Hunt', 'Outdoor adventure with clues and challenges', 'Outdoor', 180, 15.00, 10, 50),
(6, 'Paintball', 'Team combat with paintball markers', 'Outdoor', 120, 40.00, 10, 30),
(7, 'Cooking Class', 'Learn to cook with professional chef', 'Culinary', 180, 50.00, 6, 20),
(8, 'Wine Tasting', 'Tasting and learning about wines', 'Culinary', 90, 45.00, 8, 25),
(9, 'Team Cooking Challenge', 'Groups compete in cooking challenge', 'Culinary', 150, 55.00, 8, 30),
(10, 'Outdoor Camping', 'Overnight camping with team activities', 'Adventure', 1440, 80.00, 10, 40),
(11, 'Hiking Adventure', 'Group hiking with team challenges', 'Adventure', 240, 25.00, 8, 30),
(12, 'Rafting', 'White water rafting experience', 'Adventure', 180, 60.00, 8, 20),
(13, 'Trust Falls', 'Classic team building trust exercise', 'Team Building', 60, 0.00, 10, 50),
(14, 'Problem Solving Games', 'Brain teasers and problem solving', 'Team Building', 90, 10.00, 8, 30),
(15, 'Corporate Workshop', 'Professional team building workshop', 'Team Building', 240, 35.00, 10, 40);

-- Insert Court data for paddle courts
INSERT IGNORE INTO Court (name, venue_id, is_indoor, price_per_hour, available, description, amenities) VALUES
('Arena Court 1', 18, TRUE, 50.00, TRUE, 'Indoor paddle court at Arena', 'Paddle equipment, Changing rooms, Showers'),
('Arena Court 2', 18, TRUE, 50.00, TRUE, 'Indoor paddle court at Arena', 'Paddle equipment, Changing rooms, Showers'),
('Arena Court 3', 18, FALSE, 40.00, TRUE, 'Outdoor paddle court at Arena', 'Paddle equipment, Terrace, Seating'),
('Marsa Court 1', 19, TRUE, 45.00, TRUE, 'Indoor paddle court at Marsa', 'Paddle equipment, Changing rooms'),
('Marsa Court 2', 19, FALSE, 35.00, TRUE, 'Outdoor paddle court at Marsa', 'Paddle equipment, Garden view'),
('LAC Court 1', 20, TRUE, 48.00, TRUE, 'Indoor paddle court at LAC', 'Paddle equipment, Modern facilities'),
('LAC Court 2', 20, TRUE, 48.00, TRUE, 'Indoor paddle court at LAC', 'Paddle equipment, Modern facilities'),
('LAC Court 3', 20, FALSE, 38.00, TRUE, 'Outdoor paddle court at LAC', 'Paddle equipment, Lake view, Parking');

