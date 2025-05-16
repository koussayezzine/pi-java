package tn.esprit.sirine.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.sirine.models.Bus;
import tn.esprit.sirine.services.BusService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SupprimerBusController {

    @FXML
    private TableView<Bus> tableViewBus;
    @FXML
    private TableColumn<Bus, Integer> colId;
    @FXML
    private TableColumn<Bus, String> colModele;
    @FXML
    private TableColumn<Bus, Integer> colCapacite;
    @FXML
    private TableColumn<Bus, String> colPlaque;
    @FXML
    private TableColumn<Bus, String> colType;
    @FXML
    private TableColumn<Bus, Double> colTarif;
    @FXML
    private TableColumn<Bus, Integer> colItineraire;
    @FXML
    private TextField searchField;
    @FXML
    private Label snackbarLabel;
    @FXML
    private Label modeleLabel;
    @FXML
    private Label plaqueLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private PieChart statsChart;

    private final BusService busService = new BusService();
    private FilteredList<Bus> filteredList;

    @FXML
    public void initialize() {
        try {
            // Configurer les colonnes du tableau
            if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idBus"));
            if (colModele != null) colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
            if (colCapacite != null) colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
            if (colPlaque != null) colPlaque.setCellValueFactory(new PropertyValueFactory<>("plaqueImmatriculation"));
            if (colType != null) colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            if (colTarif != null) colTarif.setCellValueFactory(new PropertyValueFactory<>("tarif"));
            if (colItineraire != null) colItineraire.setCellValueFactory(new PropertyValueFactory<>("idItineraire"));
            
            // Charger les données
            loadBusData();
            
            // Ajouter un écouteur pour la sélection
            if (tableViewBus != null) {
                tableViewBus.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateBusDetails(newSelection);
                    }
                });
            }
            
            System.out.println("Initialisation de SupprimerBusController terminée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de SupprimerBusController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBusData() {
        try {
            List<Bus> buses = busService.recuperer();
            filteredList = new FilteredList<>(FXCollections.observableArrayList(buses), p -> true);
            tableViewBus.setItems(filteredList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des bus : " + e.getMessage());
        }
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(bus -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (bus.getModele().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (bus.getPlaqueImmat().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (bus.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }
    
    @FXML
    private void handleSearch() {
        // La recherche est déjà gérée par le listener
    }
    
    private void updateBusDetails(Bus bus) {
        if (bus != null) {
            // Vérifiez que les labels existent avant de les utiliser
            if (modeleLabel != null) modeleLabel.setText(bus.getModele());
            if (plaqueLabel != null) plaqueLabel.setText(bus.getPlaqueImmat());
            if (typeLabel != null) typeLabel.setText(bus.getType());
            // Ne pas utiliser idLabel s'il n'existe pas dans le FXML
            // if (idLabel != null) idLabel.setText(String.valueOf(bus.getIdBus()));
        }
    }
    
    private void updateStatsChart(Bus bus) {
        // Exemple de données pour le graphique
        PieChart.Data data1 = new PieChart.Data("En service", 75);
        PieChart.Data data2 = new PieChart.Data("En maintenance", 15);
        PieChart.Data data3 = new PieChart.Data("Hors service", 10);
        
        statsChart.setData(FXCollections.observableArrayList(data1, data2, data3));
    }

    @FXML
    private void supprimerBus() {
        Bus selectedBus = tableViewBus.getSelectionModel().getSelectedItem();
        if (selectedBus != null) {
            try {
                busService.supprimer(selectedBus.getIdBus());
                showSnackbar("✅ Bus supprimé avec succès !");
                loadBusData();
                
                // Réinitialiser les détails
                // Si idLabel n'existe pas, ne pas l'utiliser
                 // idLabel.setText("--");
                modeleLabel.setText("--");
                plaqueLabel.setText("--");
                typeLabel.setText("--");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            showSnackbar("⚠️ Veuillez sélectionner un bus à supprimer.");
        }
    }
    
    @FXML
    private void annuler() {
        tableViewBus.getSelectionModel().clearSelection();
        // Si idLabel n'existe pas, ne pas l'utiliser
        // idLabel.setText("--");
        modeleLabel.setText("--");
        plaqueLabel.setText("--");
        typeLabel.setText("--");
    }
    
    public void setBusToDelete(Bus bus) {
        System.out.println("setBusToDelete appelé avec bus: " + (bus != null ? bus.getIdBus() : "null"));
        if (bus != null) {
            try {
                // Mettre à jour les détails du bus de manière sécurisée
                Platform.runLater(() -> {
                    updateBusDetails(bus);
                    updateStatsChart(bus);
                    
                    // Sélectionner le bus dans le tableau après chargement des données
                    if (tableViewBus != null && tableViewBus.getItems() != null) {
                        tableViewBus.getItems().stream()
                            .filter(b -> b.getIdBus() == bus.getIdBus())
                            .findFirst()
                            .ifPresent(b -> tableViewBus.getSelectionModel().select(b));
                    }
                });
            } catch (Exception e) {
                System.err.println("Erreur dans setBusToDelete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void retourListe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AfficherBus.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/styleW.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la navigation: " + e.getMessage());
        }
    }

    private void showSnackbar(String message) {
        snackbarLabel.setText(message);
        snackbarLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> snackbarLabel.setVisible(false));
        pause.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

