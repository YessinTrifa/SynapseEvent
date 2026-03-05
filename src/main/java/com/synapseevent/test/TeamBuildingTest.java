package com.synapseevent.test;

import com.synapseevent.dao.TeamBuildingEventDAO;
import com.synapseevent.dao.ReservationDAO;
import com.synapseevent.entities.Event;

import java.util.List;

/**
 * Classe de test pour vérifier le fonctionnement du système de réservation Team Building
 */
public class TeamBuildingTest {
    
    public static void main(String[] args) {
        System.out.println("=== Test du système Team Building ===\n");
        
        // Test 1: Charger les événements Team Building disponibles
        testLoadTeamBuildingEvents();
        
        // Test 2: Tester une réservation
        testReservation();
        
        System.out.println("=== Tests terminés ===");
    }
    
    private static void testLoadTeamBuildingEvents() {
        System.out.println("Test 1: Chargement des événements Team Building disponibles");
        
        TeamBuildingEventDAO dao = new TeamBuildingEventDAO();
        List<Event> events = dao.findTeamBuildingEventsAvailable();
        
        System.out.println("Nombre d'événements trouvés: " + events.size());
        
        for (Event event : events) {
            System.out.println("----------------------------------------");
            System.out.println("ID: " + event.getId());
            System.out.println("Nom: " + event.getName());
            System.out.println("Date: " + event.getDate());
            System.out.println("Heure: " + event.getStartTime() + " - " + event.getEndTime());
            System.out.println("Lieu: " + event.getLocation() + ", " + event.getCity());
            System.out.println("Capacité: " + event.getCapacity() + " places");
            System.out.println("Prix: " + event.getPrice() + " TND");
            System.out.println("Organisateur: " + event.getOrganizer());
            System.out.println("Description: " + event.getDescription());
        }
        System.out.println();
    }
    
    private static void testReservation() {
        System.out.println("Test 2: Test de réservation");
        
        // Simuler une réservation pour le premier événement
        TeamBuildingEventDAO eventDao = new TeamBuildingEventDAO();
        List<Event> events = eventDao.findTeamBuildingEventsAvailable();
        
        if (!events.isEmpty()) {
            Event firstEvent = events.get(0);
            System.out.println("Tentative de réservation pour: " + firstEvent.getName());
            
            ReservationDAO reservationDao = new ReservationDAO();
            boolean success = reservationDao.reserve(
                firstEvent.getId().intValue(), 
                1, // User ID 1 pour le test
                2, // 2 places
                "TEAMBUILDING"
            );
            
            if (success) {
                System.out.println("✅ Réservation réussie!");
                
                // Vérifier la capacité mise à jour
                Event updatedEvent = eventDao.findById(firstEvent.getId().intValue());
                System.out.println("Places disponibles après réservation: " + updatedEvent.getCapacity());
            } else {
                System.out.println("❌ Échec de la réservation");
            }
        } else {
            System.out.println("Aucun événement disponible pour tester la réservation");
        }
        System.out.println();
    }
}
