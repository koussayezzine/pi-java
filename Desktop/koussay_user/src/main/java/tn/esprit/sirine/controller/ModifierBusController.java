package tn.esprit.sirine.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Bus;
import tn.esprit.sirine.models.Itineraire;
import tn.esprit.sirine.services.BusService;
import tn.esprit.sirine.services.ItineraireService;
import tn.esprit.sirine.utils.DataRefreshManager;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.geometry.Pos;

public class ModifierBusController {

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
    private ComboBox<String> statusComboBox;

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    @FXML
    private StackPane mapContainer;
    private Canvas mapCanvas;
    private GraphicsContext gc;
    private double mapClickX, mapClickY;

    private BusService busService;
    private ItineraireService itineraireService;
    private Bus selectedBus;

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
        
        // Initialiser les statuts de bus
        statusComboBox.setItems(FXCollections.observableArrayList(
            "Disponible",
            "En service",
            "En maintenance",
            "Hors service"
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
                        setText(item.getVilleDepart() + " - " + item.getVilleArrivee());
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
                        setText(item.getVilleDepart() + " - " + item.getVilleArrivee());
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des itinéraires: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Initialiser la carte
        initializeMap();
    }

    private void initializeMap() {
        if (mapContainer != null) {
            mapCanvas = new Canvas(400, 300);
            mapContainer.getChildren().add(mapCanvas);
            gc = mapCanvas.getGraphicsContext2D();
            
            // Dessiner la carte de base
            drawMap();
            
            // Ajouter un gestionnaire de clic pour définir la position
            mapCanvas.setOnMouseClicked(event -> {
                mapClickX = event.getX();
                mapClickY = event.getY();
                
                // Convertir les coordonnées du clic en latitude/longitude (simulation)
                double latitude = 36.0 + (mapClickY / mapCanvas.getHeight()) * 1.5;
                double longitude = 10.0 + (mapClickX / mapCanvas.getWidth()) * 1.0;
                
                // Mettre à jour les champs de texte
                latitudeField.setText(String.format("%.4f", latitude));
                longitudeField.setText(String.format("%.4f", longitude));
                
                // Redessiner la carte avec le marqueur
                drawMap();
                drawMarker(mapClickX, mapClickY);
            });
        }
    }

    private void drawMap() {
        if (gc == null) return;
        
        // Effacer le canvas
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner un fond pour la carte
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner les contours de la Tunisie (simplifié)
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);
        gc.beginPath();
        
        // Points approximatifs pour le contour de la Tunisie
        double[][] points = {
            {200, 20}, {250, 50}, {280, 100}, {270, 150}, 
            {250, 200}, {220, 250}, {180, 280}, {150, 250}, 
            {130, 200}, {120, 150}, {130, 100}, {150, 50}
        };
        
        gc.moveTo(points[0][0], points[0][1]);
        for (int i = 1; i < points.length; i++) {
            gc.lineTo(points[i][0], points[i][1]);
        }
        gc.closePath();
        gc.stroke();
        
        // Dessiner quelques villes principales
        drawCity(200, 50, "Tunis");
        drawCity(220, 150, "Sousse");
        drawCity(180, 200, "Sfax");
        drawCity(150, 250, "Gabès");
    }

    private void drawCity(double x, double y, String name) {
        gc.setFill(Color.RED);
        gc.fillOval(x - 5, y - 5, 10, 10);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(12));
        gc.fillText(name, x + 10, y + 5);
    }

    private void drawMarker(double x, double y) {
        gc.setFill(Color.GREEN);
        gc.fillOval(x - 8, y - 8, 16, 16);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(x - 8, y - 8, 16, 16);
        gc.setFill(Color.BLACK);
        gc.fillText("Position du bus", x + 10, y - 10);
    }

    public void setBus(Bus bus) {
        this.selectedBus = bus;
        
        if (bus == null) {
            showAlert("Erreur", "Le bus sélectionné est null", Alert.AlertType.ERROR);
            return;
        }
        
        System.out.println("Configuration du bus ID=" + bus.getIdBus() + " pour modification");
        
        // Remplir les champs avec les données du bus sélectionné
        modeleField.setText(bus.getModele());
        capaciteField.setText(String.valueOf(bus.getCapacite()));
        plaqueImmatField.setText(bus.getPlaqueImmat());
        typeComboBox.setValue(bus.getType());
        tarifField.setText(String.valueOf(bus.getTarif()));
        statusComboBox.setValue(bus.getStatus());
        
        // Définir des valeurs par défaut pour la position
        latitudeField.setText("36.8065");
        longitudeField.setText("10.1815");
        
        // Mettre à jour la carte avec la position par défaut
        if (mapCanvas != null) {
            double x = (10.1815 - 10.0) / 1.0 * mapCanvas.getWidth();
            double y = (36.8065 - 36.0) / 1.5 * mapCanvas.getHeight();
            drawMap();
            drawMarker(x, y);
        }
        
        // Sélectionner l'itinéraire correspondant
        try {
            List<Itineraire> itineraires = itineraireService.recuperer();
            for (Itineraire itineraire : itineraires) {
                if (itineraire.getIdItin() == bus.getIdItineraire()) {
                    itineraireComboBox.setValue(itineraire);
                    System.out.println("Itinéraire trouvé et sélectionné: " + itineraire.getVilleDepart() + " - " + itineraire.getVilleArrivee());
                    break;
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement de l'itinéraire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modifierBus(ActionEvent event) {
        if (selectedBus == null) {
            showAlert("Erreur", "Aucun bus sélectionné", Alert.AlertType.ERROR);
            return;
        }

        // Validation des champs
        if (modeleField.getText().isEmpty() || capaciteField.getText().isEmpty() ||
            plaqueImmatField.getText().isEmpty() || typeComboBox.getValue() == null ||
            tarifField.getText().isEmpty() || itineraireComboBox.getValue() == null ||
            statusComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Mettre à jour le bus
            selectedBus.setModele(modeleField.getText());
            selectedBus.setCapacite(Integer.parseInt(capaciteField.getText()));
            selectedBus.setPlaqueImmat(plaqueImmatField.getText());
            selectedBus.setType(typeComboBox.getValue());
            selectedBus.setTarif(Double.parseDouble(tarifField.getText()));
            selectedBus.setIdItineraire(itineraireComboBox.getValue().getIdItin());
            selectedBus.setStatus(statusComboBox.getValue());

            // Modifier le bus dans la base de données
            busService.modifier(selectedBus);
            
            // Notifier le changement de données
            if (DataRefreshManager.getInstance() != null) {
                DataRefreshManager.getInstance().notifyChange("bus", null);
            }
            
            showAlert("Succès", "Bus modifié avec succès!", Alert.AlertType.INFORMATION);
            	retour(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour la capacité et le tarif", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification du bus: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AfficherBus.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/styleW.css").toExternalForm());
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

    @FXML
    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/AfficherBus.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/styleW.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void definirPosition(ActionEvent event) {
        try {
            double latitude = Double.parseDouble(latitudeField.getText());
            double longitude = Double.parseDouble(longitudeField.getText());
            
            // Convertir latitude/longitude en coordonnées sur la carte
            double x = (longitude - 10.0) / 1.0 * mapCanvas.getWidth();
            double y = (latitude - 36.0) / 1.5 * mapCanvas.getHeight();
            
            // Redessiner la carte avec le marqueur
            drawMap();
            drawMarker(x, y);
            
            // Simuler une requête API
            simulerRequeteAPI(latitude, longitude);
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des coordonnées valides", Alert.AlertType.ERROR);
        }
    }

    private void simulerRequeteAPI(double latitude, double longitude) {
        // Simuler un délai de requête API
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Envoi de la requête à l'API de géolocalisation...");
                Thread.sleep(1000);
                updateMessage("Récupération des données de localisation...");
                Thread.sleep(1000);
                updateMessage("Validation des coordonnées...");
                Thread.sleep(500);
                return null;
            }
        };
        
        // Afficher un indicateur de progression
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label statusLabel = new Label();
        statusLabel.textProperty().bind(task.messageProperty());
        
        VBox progressBox = new VBox(10, progressIndicator, statusLabel);
        progressBox.setAlignment(Pos.CENTER);
        
        // Créer une boîte de dialogue
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Traitement en cours");
        dialog.getDialogPane().setContent(progressBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        // Fermer la boîte de dialogue lorsque la tâche est terminée
        task.setOnSucceeded(e -> {
            dialog.close();
            showAlert("Succès", "Position définie avec succès: Lat " + latitude + ", Long " + longitude, Alert.AlertType.INFORMATION);
        });
        
        // Démarrer la tâche
        new Thread(task).start();
        dialog.show();
    }
}
