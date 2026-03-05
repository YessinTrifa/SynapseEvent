# 🐛 Guide de Debug - Team Building Events

## 🔍 Problème identifié
```
DEBUG: Found 0 teambuilding events
```

## 📋 Étapes de diagnostic

### 1. Vérifier la connexion à la base de données
```bash
# Exécuter la classe de debug
java -cp . com.synapseevent.debug.DebugTeamBuildingDAO
```

### 2. Insérer les données de test manuellement
```bash
# Exécuter l'insertion des données
java -cp . com.synapseevent.debug.InsertTestData
```

### 3. Vérifier avec SQL
```bash
# Exécuter le script de debug SQL
mysql -u username -p database_name < debug.sql
```

## 🔧 Causes possibles et solutions

### Cause 1: Les données n'ont pas été insérées
**Symptôme**: La table TeamBuildingEvent est vide
**Solution**: 
```bash
# Exécuter InsertTestData.java
java com.synapseevent.debug.InsertTestData
```

### Cause 2: Les venues ne correspondent pas
**Symptôme**: Les événements existent mais pas de correspondance avec les venues
**Solution**: Vérifier que les noms de location correspondent exactement aux noms dans la table Venue

### Cause 3: La date est dans le passé
**Symptôme**: Les événements existent mais sont filtrés par date
**Solution**: Mettre à jour les dates dans les données de test

### Cause 4: Le statut n'est pas 'published'
**Symptôme**: Les événements existent mais ont un autre statut
**Solution**: 
```sql
UPDATE TeamBuildingEvent SET status = 'published' WHERE status != 'published';
```

### Cause 5: La capacité est à 0
**Symptôme**: Les événements existent mais sont pleins
**Solution**: 
```sql
UPDATE TeamBuildingEvent SET capacity = 20 WHERE capacity <= 0;
```

## 🚀 Commandes rapides

### Forcer l'insertion des données:
```bash
cd C:\Users\user\IdeaProjects\SynapseEvent
javac -cp . src\main\java\com\synapseevent\debug\InsertTestData.java
java -cp . com.synapseevent.debug.InsertTestData
```

### Vérifier l'état actuel:
```bash
javac -cp . src\main\java\com\synapseevent\debug\DebugTeamBuildingDAO.java
java -cp . com.synapseevent.debug.DebugTeamBuildingDAO
```

### Test manuel SQL:
```sql
-- Vérifier les événements
SELECT * FROM TeamBuildingEvent;

-- Vérifier les venues
SELECT * FROM Venue WHERE name LIKE '%Escape%';

-- Insérer manuellement si nécessaire
INSERT INTO TeamBuildingEvent (id, name, date, start_time, end_time, location, capacity, price, status, description, organizer) 
VALUES (1, 'Test Event', CURDATE() + INTERVAL 1 DAY, '09:00:00', '17:00:00', 'Test Location', 20, 100.00, 'published', 'Test description', 'Test Organizer');
```

## 📊 Résultat attendu

Après correction, vous devriez voir:
```
DEBUG: Found 5 teambuilding events
DEBUG: Loading event: Escape Room Challenge (ID: 1)
DEBUG: Loading event: Beach Olympics (ID: 2)
DEBUG: Loading event: Cooking Masterclass (ID: 3)
DEBUG: Loading event: Rock Climbing Adventure (ID: 4)
DEBUG: Loading event: Innovation Workshop (ID: 5)
```

## 🎯 Étapes suivantes

1. **Exécuter InsertTestData.java** pour insérer les données
2. **Redémarrer l'application** 
3. **Tester la navigation** vers Team Building
4. **Vérifier les réservations** fonctionnent

Si le problème persiste, vérifiez:
- La connexion à la base de données
- Les permissions de la base de données
- La structure exacte des tables
