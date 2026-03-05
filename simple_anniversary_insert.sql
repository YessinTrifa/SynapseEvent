-- Insertion simple pour Anniversary (sans dépendance aux venues)

-- 1. Insérer directement les événements Anniversary
INSERT IGNORE INTO AnniversaryEvent (id, name, date, start_time, end_time, location, capacity, price, organizer, description, status, category) 
VALUES 
(1, 'Golden Anniversary Gala', '2026-03-18', '19:00:00', '23:00:00', 'Tunis', 100, 150.00, 'Elite Events', 'Elegant golden anniversary celebration', 'published', 'Golden Jubilee'),
(2, 'Silver Anniversary Dinner', '2026-03-25', '18:30:00', '23:30:00', 'Sousse', 60, 95.00, 'Silver Celebrations', 'Intimate silver anniversary dinner', 'published', 'Silver Jubilee'),
(3, 'Diamond Anniversary Ball', '2026-04-01', '20:00:00', '02:00:00', 'Hammamet', 120, 180.00, 'Diamond Events', 'Luxurious diamond anniversary ball', 'published', 'Diamond Jubilee'),
(4, 'Family Anniversary Picnic', '2026-04-08', '12:00:00', '18:00:00', 'Tunis', 80, 40.00, 'Family Fun', 'Family-friendly anniversary picnic', 'published', 'Family Celebration'),
(5, 'Romantic Anniversary Evening', '2026-04-15', '19:00:00', '00:00:00', 'Sousse', 40, 200.00, 'Romantic Events', 'Intimate romantic anniversary celebration', 'published', 'Romantic Evening');

-- 2. Vérification
SELECT '=== ANNIVERSARY EVENTS INSÉRÉS ===' as info;
SELECT 
    id, 
    name, 
    DATE_FORMAT(date, '%Y-%m-%d') as event_date, 
    capacity, 
    price, 
    status, 
    category 
FROM AnniversaryEvent 
WHERE status = 'published' 
ORDER BY date;
