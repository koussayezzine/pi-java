package tn.esprit.sirine.controller;

import tn.esprit.sirine.models.BusLine;
import tn.esprit.sirine.models.User;
import tn.esprit.sirine.services.BusLineService;
import tn.esprit.sirine.utils.CurrentSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChauffeurDashboardController implements Initializable {

    @FXML
    private TableView<BusLine> busLinesTableView;

    @FXML
    private TableColumn<BusLine, String> nameColumn;

    @FXML
    private TableColumn<BusLine, String> startPointColumn;

    @FXML
    private TableColumn<BusLine, String> endPointColumn;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label tripCountLabel;

    @FXML
    private Label mileageLabel;

    @FXML
    private Label passengersLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label nextTripNameLabel;

    @FXML
    private Label nextTripTimeLabel;

    @FXML
    private Label nextTripBusLabel;

    @FXML
    private Label nextTripStatusLabel;

    @FXML
    private ProgressIndicator tripProgressIndicator;

    @FXML
    private LineChart<String, Number> performanceChart;

    private BusLineService busLineService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize the bus line service
            busLineService = new BusLineService();

            // Ensure the bus_lines table exists
            busLineService.createBusLinesTableIfNotExists();

            // Set up the table columns
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            startPointColumn.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
            endPointColumn.setCellValueFactory(new PropertyValueFactory<>("endPoint"));

            // Set welcome message with user's name
            User currentUser = CurrentSession.getInstance().getUser();
            if (currentUser != null) {
                welcomeLabel.setText("Bienvenue, " + currentUser.getUsername());
                System.out.println("Current user: " + currentUser.getUsername() + ", Role: " + currentUser.getRole());
            } else {
                welcomeLabel.setText("Bienvenue, Chauffeur");
                System.out.println("Warning: Current user is null in ChauffeurDashboardController");
            }

            // Set some example data for the dashboard
            tripCountLabel.setText("24");
            mileageLabel.setText("1,250 km");
            passengersLabel.setText("520");
            ratingLabel.setText("4.8/5");

            // Set next trip information
            nextTripNameLabel.setText("Tunis - La Marsa");
            nextTripTimeLabel.setText("Aujourd'hui, 15:30");
            nextTripBusLabel.setText("Bus #A245");
            nextTripStatusLabel.setText("Planifié");

            // Set progress indicator
            tripProgressIndicator.setProgress(0.0);

            // Load bus lines for the current chauffeur
            loadBusLinesForCurrentChauffeur();
        } catch (Exception e) {
            System.err.println("Error initializing chauffeur dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBusLinesForCurrentChauffeur() {
        try {
            // Get the current user ID from the session
            User currentUser = CurrentSession.getInstance().getUser();
            if (currentUser == null) {
                System.err.println("Cannot load bus lines: Current user is null");
                return;
            }

            int chauffeurId = currentUser.getId();
            System.out.println("Loading bus lines for chauffeur ID: " + chauffeurId);

            // Get bus lines for this chauffeur
            List<BusLine> busLines = busLineService.getBusLinesByChauffeurId(chauffeurId);
            System.out.println("Found " + busLines.size() + " bus lines for chauffeur");

            // Update the table view
            ObservableList<BusLine> observableBusLines = FXCollections.observableArrayList(busLines);
            busLinesTableView.setItems(observableBusLines);
        } catch (Exception e) {
            System.err.println("Error loading bus lines: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Event handler for viewing the route details
     */
    @FXML
    private void handleViewRoute(ActionEvent event) {
        BusLine selectedLine = busLinesTableView.getSelectionModel().getSelectedItem();
        if (selectedLine != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Itinéraire");
            alert.setHeaderText("Détails de l'itinéraire");
            alert.setContentText("Ligne: " + selectedLine.getName() + "\n" +
                    "Départ: " + selectedLine.getStartPoint() + "\n" +
                    "Arrivée: " + selectedLine.getEndPoint());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune ligne sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une ligne pour voir son itinéraire.");
            alert.showAndWait();
        }
    }

    /**
     * Event handler for starting a trip
     */
    @FXML
    private void handleStartTrip(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Démarrage du trajet");
        alert.setHeaderText(null);
        alert.setContentText("Le trajet a été démarré avec succès.");
        alert.showAndWait();

        // Update the status label
        nextTripStatusLabel.setText("En cours");
        tripProgressIndicator.setProgress(0.25);
    }

    /**
     * Event handler for viewing trip history
     */
    @FXML
    private void handleViewHistory(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historique des trajets");
        alert.setHeaderText(null);
        alert.setContentText("Fonctionnalité d'historique des trajets à implémenter.");
        alert.showAndWait();
    }
}
