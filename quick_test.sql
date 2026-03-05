-- Quick test script for Team Building events
-- Run this directly in your MySQL client

-- 1. Check if table exists
SHOW TABLES LIKE 'TeamBuildingEvent';

-- 2. Count current records
SELECT COUNT(*) as total_events FROM TeamBuildingEvent;

-- 3. If count is 0, insert test data
INSERT IGNORE INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) VALUES 
(1, 'Escape Room Challenge', '2026-03-16', '14:00:00', '18:00:00', 'Escape Room Center Tunis', 15, 75.00, 'published', 'Team building through puzzle solving', 'SynapseEvent Team'),
(2, 'Beach Olympics', '2026-03-23', '09:00:00', '17:00:00', 'Sousse Beach Resort', 40, 120.00, 'published', 'Outdoor team building games', 'Adventure Plus'),
(3, 'Cooking Masterclass', '2026-03-30', '10:00:00', '14:00:00', 'Culinary Institute Tunis', 20, 95.00, 'published', 'Collaborative cooking experience', 'Gourmet Team');

-- 4. Insert corresponding venues
INSERT IGNORE INTO Venue (id, name, type, address, city) VALUES 
(3, 'Escape Room Center Tunis', 'CLUB', 'Avenue Habib Bourguiba', 'Tunis'),
(4, 'Sousse Beach Resort', 'BEACH', 'Port El Kantaoui', 'Sousse'),
(5, 'Culinary Institute Tunis', 'HOTEL', 'Rue du Lac', 'Tunis');

-- 5. Verify insertion
SELECT '=== TEAM BUILDING EVENTS ===' as info;
SELECT id, name, DATE_FORMAT(date, '%Y-%m-%d') as event_date, status, capacity, price, location 
FROM TeamBuildingEvent 
WHERE status = 'published' 
ORDER BY date;

-- 6. Test the main query
SELECT '=== MAIN QUERY RESULT ===' as info;
SELECT t.id, t.name, t.date, t.location, t.capacity, t.price, t.status,
       (t.capacity - COALESCE(r.reserved_seats, 0)) as available_seats,
       CURDATE() as today
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
ORDER BY t.date ASC;
