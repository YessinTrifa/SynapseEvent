-- Insertion simple des données Party et Anniversary
-- Copiez-collez ce code dans votre client MySQL

-- 1. Insérer les venues
INSERT IGNORE INTO Venue (id, name, type, address, city) VALUES 
(11, 'Neon Night Club', 'CLUB', 'Avenue Habib Bourguiba, Centre Ville', 'Tunis'),
(12, 'Beach Party House', 'BEACH', 'Bord de mer, Hammamet', 'Hammamet'),
(13, 'Rooftop Lounge', 'HOTEL', 'Rue de la Paix, Les Berges du Lac', 'Tunis'),
(14, 'Garden Party Venue', 'CLUB', 'Parc du Belvédère', 'Tunis'),
(15, 'Anniversary Hall', 'HOTEL', 'Place de l''Indépendance', 'Sousse'),
(16, 'Private Mansion', 'HOTEL', 'Route de la Mer', 'La Marsa'),
(17, 'Beach Resort', 'BEACH', 'Port El Kantaoui', 'Sousse'),
(18, 'City Event Space', 'CLUB', 'Avenue Charles de Gaulle', 'Tunis');

-- 2. Insérer les événements Partying
INSERT IGNORE INTO PartyingEvent (id, name, date, start_time, end_time, venue_id, capacity, price, organizer, description, status, theme, music_type, age_restriction) 
VALUES 
(1, 'Neon Party Night', '2026-03-17', '22:00:00', '04:00:00', 11, 150, 45.00, 'Neon Events', 'Ultimate neon-themed party with glow sticks and blacklight decorations', 'published', 'Neon', 'Electronic', 18),
(2, 'Beach Sunset Party', '2026-03-24', '18:00:00', '23:00:00', 12, 200, 60.00, 'Beach Vibes', 'Beach party with bonfire, music, and sunset views', 'published', 'Beach', 'Reggae', 16),
(3, 'Rooftop Cocktail Evening', '2026-03-31', '19:00:00', '01:00:00', 13, 80, 85.00, 'Sky Events', 'Elegant rooftop party with panoramic city views and premium cocktails', 'published', 'Cocktail', 'Jazz', 21),
(4, 'Garden Music Festival', '2026-04-07', '15:00:00', '23:00:00', 14, 300, 35.00, 'Green Events', 'Outdoor music festival in beautiful garden setting', 'published', 'Festival', 'Various', 12),
(5, 'VIP Birthday Bash', '2026-04-14', '20:00:00', '03:00:00', 16, 50, 120.00, 'Elite Parties', 'Exclusive VIP birthday celebration with premium entertainment', 'published', 'VIP', 'Hip-Hop', 21);

-- 3. Insérer les événements Anniversary
INSERT IGNORE INTO AnniversaryEvent (id, name, date, start_time, end_time, location, capacity, price, organizer, description, status, category) 
VALUES 
(1, 'Golden Anniversary Gala', '2026-03-18', '19:00:00', '23:00:00', 'Anniversary Hall', 100, 150.00, 'Elite Events', 'Elegant golden anniversary celebration with dinner and dancing', 'published', 'Golden Jubilee'),
(2, 'Silver Anniversary Dinner', '2026-03-25', '18:30:00', '23:30:00', 'Private Mansion', 60, 95.00, 'Silver Celebrations', 'Intimate silver anniversary dinner with live music', 'published', 'Silver Jubilee'),
(3, 'Diamond Anniversary Ball', '2026-04-01', '20:00:00', '02:00:00', 'Beach Resort', 120, 180.00, 'Diamond Events', 'Luxurious diamond anniversary ball with ocean views', 'published', 'Diamond Jubilee'),
(4, 'Family Anniversary Picnic', '2026-04-08', '12:00:00', '18:00:00', 'City Event Space', 80, 40.00, 'Family Fun', 'Family-friendly anniversary picnic with games and activities', 'published', 'Family Celebration'),
(5, 'Romantic Anniversary Evening', '2026-04-15', '19:00:00', '00:00:00', 'Rooftop Lounge', 40, 200.00, 'Romantic Events', 'Intimate romantic anniversary celebration under the stars', 'published', 'Romantic Evening');

-- 4. Vérification
SELECT '=== DONNEES INSEREES AVEC SUCCÈS ===' as message;

SELECT '=== ÉVÉNEMENTS PARTYING ===' as info;
SELECT id, name, DATE_FORMAT(date, '%Y-%m-%d') as event_date, capacity, price, status, theme 
FROM PartyingEvent 
WHERE status = 'published' 
ORDER BY date;

SELECT '=== ÉVÉNEMENTS ANNIVERSARY ===' as info;
SELECT id, name, DATE_FORMAT(date, '%Y-%m-%d') as event_date, capacity, price, status, category 
FROM AnniversaryEvent 
WHERE status = 'published' 
ORDER BY date;
