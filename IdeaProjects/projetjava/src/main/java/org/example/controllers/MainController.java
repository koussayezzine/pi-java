package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private void goToReservations() {
        try {
            URL resource = getClass().getResource("/GestionReservation.fxml");
            if (resource == null) {
                System.err.println("Resource not found: /GestionReservation.fxml");
                showErrorAlert("Impossible de charger l'interface des réservations", "Le fichier GestionReservation.fxml est introuvable dans src/main/resources/.");
                return;
            }
            System.out.println("Loading resource: " + resource);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Réservations");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionReservation.fxml: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger GestionReservation.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void goToTickets() {
        try {
            URL resource = getClass().getResource("/GestionTicket.fxml");
            if (resource == null) {
                System.err.println("Resource not found: /GestionTicket.fxml");
                showErrorAlert("Impossible de charger l'interface des tickets", "Le fichier GestionTicket.fxml est introuvable dans src/main/resources/.");
                return;
            }
            System.out.println("Loading resource: " + resource);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Tickets");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de GestionTicket.fxml: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger GestionTicket.fxml: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}