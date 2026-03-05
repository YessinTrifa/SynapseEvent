# Partying & Anniversary Reservation System - Guide Complet

## 🎉 Description

Système de réservation complet pour les événements Partying et Anniversary, intégré en suivant le même pattern que Formation, Paddle et Team Building.

## 📁 Fichiers créés

### DAOs:
- `PartyingEventDAO.java` - Accès aux données des événements Partying
- `AnniversaryEventDAO.java` - Accès aux données des événements Anniversary

### Contrôleurs:
- `ReservationPartyingDashboardController.java` - Dashboard des événements Partying
- `ReservationPartyingDetailsController.java` - Détails et réservation Partying
- `ReservationAnniversaryDashboardController.java` - Dashboard des événements Anniversary
- `ReservationAnniversaryDetailsController.java` - Détails et réservation Anniversary

### FXML:
- `reservationPartyingDashboard.fxml` - Interface dashboard Partying
- `reservationPartyingDetails.fxml` - Interface détails Partying
- `reservationAnniversaryDashboard.fxml` - Interface dashboard Anniversary
- `reservationAnniversaryDetails.fxml` - Interface détails Anniversary

### Données de test:
- `partying_anniversary_test_data.sql` - Script SQL avec données de test

## 🎭 Événements Partying de test:

1. **Neon Party Night** 🎨
   - Date: 17 Mars 2026, 22:00-04:00
   - Lieu: Neon Night Club, Tunis
   - Capacité: 150 places
   - Prix: 45.00 TND
   - Thème: Neon, Musique: Electronic

2. **Beach Sunset Party** 🌅
   - Date: 24 Mars 2026, 18:00-23:00
   - Lieu: Beach Party House, Hammamet
   - Capacité: 200 places
   - Prix: 60.00 TND
   - Thème: Beach, Musique: Reggae

3. **Rooftop Cocktail Evening** 🍸
   - Date: 31 Mars 2026, 19:00-01:00
   - Lieu: Rooftop Lounge, Tunis
   - Capacité: 80 places
   - Prix: 85.00 TND
   - Thème: Cocktail, Musique: Jazz

4. **Garden Music Festival** 🌿
   - Date: 7 Avril 2026, 15:00-23:00
   - Lieu: Garden Party Venue, Tunis
   - Capacité: 300 places
   - Prix: 35.00 TND
   - Thème: Festival, Musique: Various

5. **VIP Birthday Bash** ⭐
   - Date: 14 Avril 2026, 20:00-03:00
   - Lieu: Private Mansion, La Marsa
   - Capacité: 50 places
   - Prix: 120.00 TND
   - Thème: VIP, Musique: Hip-Hop

## 🎂 Événements Anniversary de test:

1. **Golden Anniversary Gala** ✨
   - Date: 18 Mars 2026, 19:00-23:00
   - Lieu: Anniversary Hall, Sousse
   - Capacité: 100 places
   - Prix: 150.00 TND
   - Catégorie: Golden Jubilee

2. **Silver Anniversary Dinner** 🥈
   - Date: 25 Mars 2026, 18:30-23:30
   - Lieu: Private Mansion, La Marsa
   - Capacité: 60 places
   - Prix: 95.00 TND
   - Catégorie: Silver Jubilee

3. **Diamond Anniversary Ball** 💎
   - Date: 1 Avril 2026, 20:00-02:00
   - Lieu: Beach Resort, Sousse
   - Capacité: 120 places
   - Prix: 180.00 TND
   - Catégorie: Diamond Jubilee

4. **Family Anniversary Picnic** 🧺
   - Date: 8 Avril 2026, 12:00-18:00
   - Lieu: City Event Space, Tunis
   - Capacité: 80 places
   - Prix: 40.00 TND
   - Catégorie: Family Celebration

5. **Romantic Anniversary Evening** 💕
   - Date: 15 Avril 2026, 19:00-00:00
   - Lieu: Rooftop Lounge, Tunis
   - Capacité: 40 places
   - Prix: 200.00 TND
   - Catégorie: Romantic Evening

## 🚀 Comment tester

### 1. Charger les données de test

```sql
-- Exécuter le script de données de test
SOURCE partying_anniversary_test_data.sql;
```

### 2. Mettre à jour le User Dashboard

Ajouter les boutons dans le UserDashboard.fxml (My Bookings section):
```xml
<Button onAction="#openPartyingReservation" 
        styleClass="user-btn-apply" text="🎉 Réservation Party" />
<Button onAction="#openAnniversaryReservation" 
        styleClass="user-btn-apply" text="🎂 Réservation Anniversaire" />
```

### 3. Tester dans l'application

1. **User Dashboard** → **My Bookings** → Cliquer sur **"🎉 Réservation Party"**
2. **Dashboard Party** → Parcourir les événements disponibles
3. **Détails Party** → Cliquer sur un événement → Faire une réservation
4. **User Dashboard** → **My Bookings** → Cliquer sur **"🎂 Réservation Anniversaire"**
5. **Dashboard Anniversary** → Parcourir les événements disponibles
6. **Détails Anniversary** → Cliquer sur un événement → Faire une réservation

## 🔧 Fonctionnalités implémentées

### ✅ Partying System:
- Dashboard avec cartes d'événements (couleur violette #7c3aed)
- Détails avec informations complètes (thème, musique, âge requis)
- Système de réservation avec calcul du prix total
- Gestion de la capacité et disponibilité
- Navigation fluide entre dashboard et détails

### ✅ Anniversary System:
- Dashboard avec cartes d'événements (couleur rose #ec4899)
- Détails avec informations complètes (catégorie d'anniversaire)
- Système de réservation avec calcul du prix total
- Gestion de la capacité et disponibilité
- Navigation fluide entre dashboard et détails

### ✅ Integration complète:
- **UserDashboardController** → `openPartyingReservation()` et `openAnniversaryReservation()`
- **ReservationDAO** → Support des types PARTYING et ANNIVERSARY
- **EventContext** → Gestion du contexte de sélection
- **Navigator** → Navigation entre les écrans

## 🎨 Design et UX

### Couleurs et thèmes:
- **Partying**: Violet (#7c3aed) avec icône 🎉
- **Anniversary**: Rose (#ec4899) avec icône 🎂
- **Hover effects**: Transitions douces et ombres portées
- **Cards**: Design moderne avec coins arrondis

### Expérience utilisateur:
- Navigation intuitive avec boutons retour
- Dialogues de réservation avec spinner pour le nombre de places
- Calcul en temps réel du prix total
- Messages de confirmation et d'erreur clairs
- Gestion automatique de la disponibilité

## 📊 Résultat attendu

L'utilisateur devrait pouvoir:
1. Accéder aux dashboards Party et Anniversary depuis le User Dashboard
2. Voir 5 événements différents dans chaque catégorie
3. Naviguer facilement entre les dashboards et les détails
4. Faire des réservations avec confirmation immédiate
5. Voir les places disponibles se mettre à jour automatiquement

Le système est maintenant complètement intégré et prêt pour des tests complets ! 🏆
