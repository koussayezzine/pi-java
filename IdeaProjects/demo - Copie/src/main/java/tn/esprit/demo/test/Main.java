package tn.esprit.demo.test;

import tn.esprit.demo.models.Bus;
import tn.esprit.demo.services.BusService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        BusService busService = new BusService();

        // Ajouter un bus
        try {
            // idBus n'est pas requis pour l'ajout (généré automatiquement), mais les autres champs le sont
            Bus bus = new Bus(0, "Mercedes", 50, "123ABC", 1, "Type A", 9.5); // Remplace 1 (idItin) et 9.5 (tarif) selon ta BD
            busService.ajouter(bus);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du bus : " + e.getMessage());
        }

        // Modifier un bus
        try {
            Bus busModifie = new Bus(1, "Volvo", 60, "456DEF", 2, "Type B", 12.0);
            busService.modifier(busModifie);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du bus : " + e.getMessage());
        }

        // Récupérer tous les bus
        try {
            System.out.println("Liste des bus :");
            System.out.println(busService.recuperer()); // Utiliser le nom exact
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des bus : " + e.getMessage());
        }

        // Supprimer un bus
        try {
            busService.supprimer(1);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du bus : " + e.getMessage());
        }
    }
}
