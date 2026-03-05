-- Debug script pour Anniversary Events
-- Exécutez ceci pour diagnostiquer le problème

-- 1. Vérifier si la table existe et a des données
SELECT '=== TABLE ANNIVERSARYEVENT ===' as info;
SHOW TABLES LIKE 'AnniversaryEvent';

SELECT COUNT(*) as total FROM AnniversaryEvent;

-- 2. Voir tous les enregistrements
SELECT '=== CONTENU COMPLET ===' as info;
SELECT 
    id,
    name,
    DATE_FORMAT(date, '%Y-%m-%d') as event_date,
    start_time,
    end_time,
    location,
    capacity,
    price,
    status,
    category,
    organizer
FROM AnniversaryEvent 
ORDER BY id;

-- 3. Vérifier les venues correspondantes
SELECT '=== VENUES POUR ANNIVERSARY ===' as info;
SELECT 
    v.id,
    v.name,
    v.city,
    v.address
FROM Venue v
WHERE v.name IN ('Anniversary Hall', 'Private Mansion', 'Beach Resort', 'City Event Space', 'Rooftop Lounge')
ORDER BY v.id;

-- 4. Test de la requête exacte utilisée dans le code
SELECT '=== TEST REQUÊTE PRINCIPALE ===' as info;
SELECT 
    a.id, 
    a.name, 
    a.date, 
    a.start_time, 
    a.end_time, 
    a.location, 
    v.city, 
    v.address, 
    a.capacity, 
    a.price, 
    a.description, 
    a.status, 
    a.organizer,
    (a.capacity - COALESCE(r.reserved_seats, 0)) as available_seats,
    CURDATE() as today,
    CASE 
        WHEN a.status = 'published' THEN '✅ Published'
        ELSE '❌ Not published'
    END as status_check,
    CASE 
        WHEN a.date >= CURDATE() THEN '✅ Future date'
        ELSE '❌ Past date'
    END as date_check,
    CASE 
        WHEN (a.capacity - COALESCE(r.reserved_seats, 0)) > 0 THEN '✅ Available'
        ELSE '❌ Full'
    END as availability_check
FROM AnniversaryEvent a
LEFT JOIN Venue v ON a.location = v.name
LEFT JOIN (
    SELECT event_id, SUM(seats) as reserved_seats
    FROM reservations 
    WHERE status = 'CONFIRMED' AND event_type = 'ANNIVERSARY'
    GROUP BY event_id
) r ON a.id = r.event_id
WHERE a.status = 'published' 
AND a.date >= CURDATE()
AND (a.capacity - COALESCE(r.reserved_seats, 0)) > 0
ORDER BY a.date ASC, a.start_time ASC;

-- 5. Insertion manuelle si nécessaire
SELECT '=== INSERTION MANUELLE SI NÉCESSAIRE ===' as info;
INSERT IGNORE INTO AnniversaryEvent (id, name, date, start_time, end_time, location, capacity, price, organizer, description, status, category) 
VALUES 
(1, 'Test Anniversary Event', CURDATE() + INTERVAL 1 DAY, '19:00:00', '23:00:00', 'Test Location', 50, 100.00, 'Test Organizer', 'Test anniversary event', 'published', 'Test Category');

-- 6. Vérification finale
SELECT '=== VÉRIFICATION FINALE ===' as info;
SELECT COUNT(*) as published_anniversary_events 
FROM AnniversaryEvent 
WHERE status = 'published' 
AND date >= CURDATE();
