package tn.esprit.sirine.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import tn.esprit.sirine.services.ReservationService;
import tn.esprit.sirine.utils.MaConnexion;
import tn.esprit.sirine.models.Reservation;

import java.util.List;

public class SupprimerReservation {

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String> colNom;
    @FXML private TableColumn<Reservation, String> colEmail;
    @FXML private TableColumn<Reservation, String> colDepart;
    @FXML private TableColumn<Reservation, String> colArrivee;
    @FXML private TableColumn<Reservation, String> colStatut;
    @FXML private Button btnSupprimer;
    @FXML private Button btnAnnuler;
    @FXML private Label messageLabel;

    private ReservationService reservationService;
    private Runnable onReservationDeleted;

    public SupprimerReservation() {
        this.reservationService = new ReservationService(MaConnexion.getConnection());
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation de SupprimerReservation");
            colNom.setCellValueFactory(data -> {
                System.out.println("CellValueFactory pour Nom: " + data.getValue().getNomUtilisateur());
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getNomUtilisateur());
            });
            colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
            colDepart.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLieuDepart()));
            colArrivee.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLieuArrive()));
            colStatut.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatut()));

            afficherReservations();
            System.out.println("TableView initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur dans initialize: " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Erreur lors de l'initialisation du TableView.");
            messageLabel.setTextFill(Color.web("#e74c3c"));
        }
    }

    public void setOnReservationDeleted(Runnable callback) {
        this.onReservationDeleted = callback;
    }

    public void setReservationService(ReservationService service) {
        this.reservationService = service;
    }

    private void afficherReservations() {
        try {
            List<Reservation> reservations = reservationService.getAll();
            System.out.println("Nombre de réservations chargées : " + reservations.size());
            ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservations);
            tableReservations.setItems(observableList);
            messageLabel.setText("");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement des réservations : " + e.getMessage());
            messageLabel.setTextFill(Color.web("#e74c3c"));
            System.err.println("Erreur dans afficherReservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerreservation() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setText("Veuillez sélectionner une réservation à supprimer.");
            messageLabel.setTextFill(Color.web("#e74c3c"));
            return;
        }

        try {
            reservationService.delete(selected);
            afficherReservations();
            messageLabel.setText("Réservation supprimée avec succès.");
            messageLabel.setTextFill(Color.web("#2ecc71"));

            if (onReservationDeleted != null) {
                onReservationDeleted.run();
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la suppression : " + e.getMessage());
            messageLabel.setTextFill(Color.web("#e74c3c"));
            System.err.println("Erreur dans supprimerreservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulersuppression() {
        tableReservations.getSelectionModel().clearSelection();
        messageLabel.setText("");
    }
}