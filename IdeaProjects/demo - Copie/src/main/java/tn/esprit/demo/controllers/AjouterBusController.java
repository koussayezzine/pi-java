package tn.esprit.demo.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.demo.models.Bus;
import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.services.BusService;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.DataRefreshManager;

import java.io.IOException;
import java.util.List;

public class AjouterBusController {

    private BusService busService;
    private ItineraireService itineraireService;

    @FXML
    private TextField modeleField;

    @FXML
    private TextField capaciteField;

    @FXML
    private TextField plaqueImmatField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField tarifField;

    @FXML
    private ComboBox<Itineraire> itineraireComboBox;

    @FXML
    public void initialize() {
        busService = new BusService();
        itineraireService = new ItineraireService();

        // Initialiser les types de bus
        typeComboBox.setItems(FXCollections.observableArrayList(
            "Standard",
            "Confort",
            "Premium",
            "VIP"
        ));

        // Charger les itinéraires
        try {
            List<Itineraire> itineraires = itineraireService.recuperer();
            itineraireComboBox.setItems(FXCollections.observableArrayList(itineraires));
            itineraireComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Itineraire item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getPointDepart() + " - " + item.getPointArrivee());
                    }
                }
            });
            itineraireComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Itineraire item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getPointDepart() + " - " + item.getPointArrivee());
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des itinéraires: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ajouterBus(ActionEvent event) {
        // Validation des champs
        if (modeleField.getText().isEmpty() || capaciteField.getText().isEmpty() ||
            plaqueImmatField.getText().isEmpty() || typeComboBox.getValue() == null ||
            tarifField.getText().isEmpty() || itineraireComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Créer un nouveau bus
            Bus bus = new Bus();
            bus.setModele(modeleField.getText());
            bus.setCapacite(Integer.parseInt(capaciteField.getText()));
            bus.setPlaqueImmat(plaqueImmatField.getText());
            bus.setType(typeComboBox.getValue());
            bus.setTarif(Double.parseDouble(tarifField.getText()));
            bus.setIdItineraire(itineraireComboBox.getValue().getIdItin());
            bus.setStatus("Disponible"); // Définir un statut par défaut

            // Ajouter le bus
            busService.ajouter(bus);
            
            // Notifier le changement de données
            DataRefreshManager.getInstance().notifyChange("bus", null);
            
            showAlert("Succès", "Bus ajouté avec succès!", Alert.AlertType.INFORMATION);
            
            // Retourner à l'écran d'affichage
            retour(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour la capacité et le tarif", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du bus: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        modeleField.clear();
        capaciteField.clear();
        plaqueImmatField.clear();
        typeComboBox.setValue(null);
        tarifField.clear();
        itineraireComboBox.setValue(null);
    }

    @FXML
    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AfficherBus.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
