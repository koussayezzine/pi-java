package tn.esprit.sirine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.sirine.services.ReservationService;
import tn.esprit.sirine.utils.MaConnexion;
import tn.esprit.sirine.models.Reservation;

import java.io.IOException;
import java.sql.Connection;

public class GestiondesReservations {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> nomColumn;
    @FXML
    private TableColumn<Reservation, String> emailColumn;
    @FXML
    private TableColumn<Reservation, String> departColumn;
    @FXML
    private TableColumn<Reservation, String> arriveColumn;
    @FXML
    private TableColumn<Reservation, String> statutColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button btnRetour;
    @FXML
    private Label errorLabel;
    @FXML
    private Label noteLabel;

    private ReservationService reservationService;
    private ObservableList<Reservation> reservationsList;

    public GestiondesReservations() {
        Connection conn = MaConnexion.getConnection();
        reservationService = new ReservationService(conn);
        reservationsList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomUtilisateur()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        departColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieuDepart()));
        arriveColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieuArrive()));
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));

        reservationsTable.setItems(reservationsList);
        loadAllReservations();
    }

    private void loadAllReservations() {
        try {
            reservationsList.clear();
            reservationsList.addAll(reservationService.getAll());
            noteLabel.setText("Réservations chargées : " + reservationsList.size());
            errorLabel.setText("");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors du chargement des réservations.");
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void searchReservations() {
        String nomUtilisateur = searchField.getText().trim();
        if (nomUtilisateur.isEmpty()) {
            errorLabel.setText("Veuillez entrer un nom de passager.");
            noteLabel.setText("");
            return;
        }

        try {
            reservationsList.clear();
            Reservation reservation = reservationService.getByNom(nomUtilisateur);
            if (reservation == null) {
                errorLabel.setText("Aucune réservation trouvée pour ce passager.");
                noteLabel.setText("");
            } else {
                reservationsList.add(reservation);
                noteLabel.setText("Réservation trouvée.");
                errorLabel.setText("");
            }
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de la recherche des réservations.");
            noteLabel.setText("");
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addReservation() {
        try {
            // Charger l'interface personnalisée AjouterReservation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AjouterReservation.fxml"));
            Parent root = loader.load();

            // Accéder au contrôleur pour passer le ReservationService et la méthode de rappel
            AjouterReservation controller = loader.getController();
            controller.setReservationService(reservationService);
            controller.setOnReservationAdded(this::loadAllReservations);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Réservation");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface d'ajout.");
            System.err.println("Erreur lors du chargement de AjouterReservation.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void editReservation() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Veuillez sélectionner une réservation.");
            noteLabel.setText("");
            return;
        }

        try {
            // Charger l'interface personnalisée ModifierReservation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/ModifierReservation.fxml"));
            Parent root = loader.load();

            // Accéder au contrôleur pour passer la réservation sélectionnée et la méthode de rappel
            tn.esprit.sirine.controller.ModifierReservation controller = loader.getController();
            controller.setReservationService(reservationService);
            controller.setOnReservationModified(this::loadAllReservations);
            controller.setReservationToEdit(selected);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la Réservation");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface de modification.");
            System.err.println("Erreur lors du chargement de ModifierReservation.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteReservation() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Veuillez sélectionner une réservation.");
            noteLabel.setText("");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer la réservation #" + selected.getIdReservation());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                reservationService.delete(selected);
                loadAllReservations();
                noteLabel.setText("Réservation supprimée avec succès.");
                errorLabel.setText("");
            } catch (Exception e) {
                errorLabel.setText("Erreur lors de la suppression de la réservation.");
                System.err.println("Erreur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}