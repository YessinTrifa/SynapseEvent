-- Script de debug pour vérifier les données Team Building

-- 1. Vérifier si la table TeamBuildingEvent existe
SHOW TABLES LIKE 'TeamBuildingEvent';

-- 2. Compter les enregistrements
SELECT COUNT(*) as total_events FROM TeamBuildingEvent;

-- 3. Voir tous les événements Team Building
SELECT 
    id,
    name,
    DATE_FORMAT(date, '%Y-%m-%d') as event_date,
    TIME_FORMAT(start_time, '%H:%i') as start_time,
    TIME_FORMAT(end_time, '%H:%i') as end_time,
    location,
    capacity,
    price,
    status,
    organizer
FROM TeamBuildingEvent
ORDER BY id;

-- 4. Vérifier les venues correspondantes
SELECT 
    v.id,
    v.name,
    v.city,
    v.address
FROM Venue v
WHERE v.name IN (
    'Escape Room Center Tunis',
    'Sousse Beach Resort', 
    'Culinary Institute Tunis',
    'Indoor Climbing Gym Tunis',
    'Innovation Lab Tunis'
)
ORDER BY v.id;

-- 5. Vérifier les réservations TEAMBUILDING
SELECT 
    r.event_id,
    r.seats,
    r.status,
    r.event_type,
    r.created_at
FROM reservations r
WHERE r.event_type = 'TEAMBUILDING'
ORDER BY r.event_id;

-- 6. Test de la requête principale (debug)
SELECT 
    t.id,
    t.name,
    t.date,
    t.start_time,
    t.end_time,
    t.location,
    v.city,
    v.address,
    t.capacity,
    t.price,
    t.description,
    t.status,
    t.organizer,
    (t.capacity - COALESCE(r.reserved_seats, 0)) as available_seats,
    CURDATE() as today,
    CASE 
        WHEN t.status = 'published' THEN '✅ Published'
        ELSE '❌ Not published'
    END as status_check,
    CASE 
        WHEN t.date >= CURDATE() THEN '✅ Future date'
        ELSE '❌ Past date'
    END as date_check,
    CASE 
        WHEN (t.capacity - COALESCE(r.reserved_seats, 0)) > 0 THEN '✅ Available'
        ELSE '❌ Full'
    END as availability_check
FROM TeamBuildingEvent t
LEFT JOIN Venue v ON t.location = v.name
LEFT JOIN (
    SELECT event_id, SUM(seats) as reserved_seats
    FROM reservations 
    WHERE status = 'CONFIRMED' AND event_type = 'TEAMBUILDING'
    GROUP BY event_id
) r ON t.id = r.event_id
WHERE t.status = 'published' 
AND t.date >= CURDATE()
AND (t.capacity - COALESCE(r.reserved_seats, 0)) > 0
ORDER BY t.date ASC, t.start_time ASC;
