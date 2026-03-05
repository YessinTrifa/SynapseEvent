-- Données de test pour FormationEvent et TeamBuildingEvent
-- À exécuter après le schema.sql principal

INSERT IGNORE INTO FormationEvent (id, name, date, start_time, end_time, location, capacity, price, status, description) 
VALUES 
(1, 'Leadership Excellence', '2026-03-18', '09:00:00', '17:00:00', 'Conference Center Tunis', 30, 250.00, 'published', 'Advanced leadership training for managers'),
(2, 'Digital Marketing Workshop', '2026-03-22', '09:00:00', '13:00:00', 'Tech Hub Sousse', 25, 150.00, 'published', 'Learn digital marketing strategies and tools'),
(3, 'Project Management Fundamentals', '2026-03-28', '09:00:00', '17:00:00', 'Business Center Tunis', 20, 200.00, 'published', 'Essential project management skills and certification prep');

INSERT IGNORE INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) 
VALUES 
(1, 'Escape Room Challenge', '2026-03-16', '14:00:00', '18:00:00', 'Escape Room Center Tunis', 15, 75.00, 'published', 'Team building through puzzle solving and collaboration', 'SynapseEvent Team'),
(2, 'Beach Olympics', '2026-03-23', '09:00:00', '17:00:00', 'Sousse Beach Resort', 40, 120.00, 'published', 'Outdoor team building games and competitions', 'Adventure Plus'),
(3, 'Cooking Masterclass', '2026-03-30', '10:00:00', '14:00:00', 'Culinary Institute Tunis', 20, 95.00, 'published', 'Collaborative cooking experience with professional chefs', 'Gourmet Team'),
(4, 'Rock Climbing Adventure', '2026-04-06', '08:00:00', '16:00:00', 'Indoor Climbing Gym Tunis', 25, 110.00, 'published', 'Trust building through rock climbing challenges', 'Extreme Sports Co'),
(5, 'Innovation Workshop', '2026-04-13', '09:00:00', '17:00:00', 'Innovation Lab Tunis', 30, 180.00, 'published', 'Creative problem solving and design thinking exercises', 'Innovation Hub');

-- Ajout de venues supplémentaires pour les événements Team Building
INSERT IGNORE INTO Venue (id, name, type, address, city) 
VALUES 
(3, 'Escape Room Center Tunis', 'CLUB', 'Avenue Habib Bourguiba, Centre Commercial', 'Tunis'),
(4, 'Sousse Beach Resort', 'BEACH', 'Bord de mer, Port El Kantaoui', 'Sousse'),
(5, 'Culinary Institute Tunis', 'HOTEL', 'Rue du Lac, Les Berges du Lac', 'Tunis'),
(6, 'Indoor Climbing Gym Tunis', 'CLUB', 'Zone Industrielle, El Menzah', 'Tunis'),
(7, 'Innovation Lab Tunis', 'HOTEL', 'Avenue de la République', 'Tunis'),
(8, 'Conference Center Tunis', 'HOTEL', 'Place de la Constitution', 'Tunis'),
(9, 'Tech Hub Sousse', 'CLUB', 'Zone Technologique', 'Sousse'),
(10, 'Business Center Tunis', 'HOTEL', 'Centre Urbain Nord', 'Tunis');
