package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.models.Reservation;
import org.example.service.ReservationService;
import org.example.util.DBconnection;

import java.util.List;

public class AfficherReservation {

    private final ReservationService reservationService = new ReservationService(DBconnection.getInstance().getConn());

    @FXML private TableView<Reservation> tableView;
    @FXML private TableColumn<Reservation, String> colNomUtilisateur;
    @FXML private TableColumn<Reservation, String> colEmail;
    @FXML private TableColumn<Reservation, String> colLieuDepart;
    @FXML private TableColumn<Reservation, String> colLieuArrive;
    @FXML private TableColumn<Reservation, String> colStatut;

    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Liaison des colonnes avec les propriétés du modèle
        colNomUtilisateur.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomUtilisateur()));
        colEmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colLieuDepart.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLieuDepart()));
        colLieuArrive.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLieuArrive()));
        colStatut.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));

        // Chargement initial des données
        afficherTout();
    }

    public void afficherTout() {
        try {
            List<Reservation> reservations = reservationService.getAll();
            ObservableList<Reservation> observableList = FXCollections.observableList(reservations);
            tableView.setItems(observableList);
            messageLabel.setText("");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement des réservations.");
            e.printStackTrace();
        }
    }
}
