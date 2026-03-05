-- Test de connexion et de base de données
-- Exécuter ce script étape par étape

-- 1. Vérifier que nous sommes bien connectés
SELECT 'CONNECTED TO DATABASE' as status, DATABASE() as current_db;

-- 2. Lister toutes les tables
SELECT '=== ALL TABLES ===' as info;
SHOW TABLES;

-- 3. Vérifier spécifiquement la table TeamBuildingEvent
SELECT '=== TEAM BUILDING TABLE CHECK ===' as info;
SHOW TABLES LIKE 'TeamBuildingEvent';

-- 4. Si la table n'existe pas, la créer
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

-- 5. Vérifier la structure de la table
DESCRIBE TeamBuildingEvent;

-- 6. Compter les enregistrements existants
SELECT COUNT(*) as total_records FROM TeamBuildingEvent;

-- 7. Insérer un enregistrement de test simple
INSERT IGNORE INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) 
VALUES (1, 'Test Event', CURDATE() + INTERVAL 1 DAY, '09:00:00', '17:00:00', 'Test Location', 20, 100.00, 'published', 'Test description', 'Test Organizer');

-- 8. Vérifier l'insertion
SELECT '=== AFTER INSERTION ===' as info;
SELECT id, name, date, status, capacity, price FROM TeamBuildingEvent;

-- 9. Test de la requête exacte utilisée dans le code
SELECT '=== TESTING EXACT QUERY ===' as info;
SELECT t.id, t.name, t.date, t.start_time, t.end_time, 
       t.location, t.capacity, t.price, t.description, t.status, t.organizer,
       (t.capacity - COALESCE(r.reserved_seats, 0)) as available_seats
FROM TeamBuildingEvent t
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
