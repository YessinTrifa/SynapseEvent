# Team Building Reservation System - Guide de Test

## 📋 Description

Ce document explique comment tester le système de réservation Team Building qui a été intégré en suivant le même pattern que Formation et Paddle.

## 🗄️ Données de Test

### Fichiers créés:
- `src/main/resources/test-data.sql` - Données de test SQL
- `src/main/java/com/synapseevent/test/TeamBuildingTest.java` - Classe de test Java

### Événements Team Building disponibles:

1. **Escape Room Challenge** 
   - Date: 16 Mars 2026, 14:00-18:00
   - Lieu: Escape Room Center Tunis
   - Capacité: 15 places
   - Prix: 75.00 TND
   - Organisateur: SynapseEvent Team

2. **Beach Olympics**
   - Date: 23 Mars 2026, 09:00-17:00
   - Lieu: Sousse Beach Resort
   - Capacité: 40 places
   - Prix: 120.00 TND
   - Organisateur: Adventure Plus

3. **Cooking Masterclass**
   - Date: 30 Mars 2026, 10:00-14:00
   - Lieu: Culinary Institute Tunis
   - Capacité: 20 places
   - Prix: 95.00 TND
   - Organisateur: Gourmet Team

4. **Rock Climbing Adventure**
   - Date: 6 Avril 2026, 08:00-16:00
   - Lieu: Indoor Climbing Gym Tunis
   - Capacité: 25 places
   - Prix: 110.00 TND
   - Organisateur: Extreme Sports Co

5. **Innovation Workshop**
   - Date: 13 Avril 2026, 09:00-17:00
   - Lieu: Innovation Lab Tunis
   - Capacité: 30 places
   - Prix: 180.00 TND
   - Organisateur: Innovation Hub

## 🚀 Comment tester

### 1. Charger les données de test

```sql
-- Exécuter d'abord le schéma principal
SOURCE schema.sql;

-- Puis charger les données de test
SOURCE test-data.sql;
```

### 2. Lancer le test Java

```bash
# Compiler et exécuter la classe de test
javac -cp . src/main/java/com/synapseevent/test/TeamBuildingTest.java
java -cp . com.synapseevent.test.TeamBuildingTest
```

### 3. Tester dans l'application

1. Démarrer l'application SynapseEvent
2. Se connecter avec un utilisateur
3. Dans le User Dashboard, cliquer sur **"🤝 Réservation Team Building"**
4. Parcourir les événements disponibles
5. Cliquer sur un événement pour voir les détails
6. Faire une réservation en sélectionnant le nombre de places

## 🔧 Fonctionnalités testées

- ✅ Affichage des événements Team Building disponibles
- ✅ Navigation vers les détails d'un événement
- ✅ Système de réservation avec sélection du nombre de places
- ✅ Calcul automatique du prix total
- ✅ Mise à jour de la capacité disponible
- ✅ Gestion des erreurs (capacité insuffisante, etc.)

## 📊 Intégration complète

Le système Team Building est maintenant complètement intégré:

- **UserDashboardController** → `openTeamBuildingReservation()`
- **ReservationTeamBuildingDashboardController** → Affichage des événements
- **ReservationTeamBuildingDetailsController** → Détails et réservation
- **ReservationDAO** → Générique pour PADDLE, FORMATION, TEAMBUILDING
- **TeamBuildingEventDAO** → Accès aux données Team Building

## 🎯 Résultat attendu

L'utilisateur devrait pouvoir:
1. Voir 5 événements Team Building différents
2. Naviguer entre le dashboard et les détails
3. Faire des réservations avec confirmation
4. Voir les places disponibles se mettre à jour
5. Recevoir des messages d'erreur appropriés

Le système suit exactement le même pattern que Formation et Paddle pour une expérience utilisateur cohérente !
