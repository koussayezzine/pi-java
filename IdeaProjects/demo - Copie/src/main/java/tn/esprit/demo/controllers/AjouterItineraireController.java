package tn.esprit.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.TransportAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class AjouterItineraireController implements Initializable {

    private ItineraireService itineraireService;

    @FXML
    private TextField villeDepartField;
    @FXML
    private TextField villeArriveeField;
    @FXML
    private TextField arretsField;
    @FXML
    private TextField distanceField;
    @FXML
    private TextField dureeField;
    @FXML
    private Button ajouterButton;
    @FXML
    private StackPane mapContainer;
    private Canvas mapCanvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation du contrôleur AjouterItineraireController ===");
        try {
            itineraireService = new ItineraireService();
            
            // Vérification détaillée des champs
            System.out.println("Vérification des champs FXML:");
            System.out.println("villeDepartField: " + (villeDepartField != null ? "OK" : "NULL"));
            System.out.println("villeArriveeField: " + (villeArriveeField != null ? "OK" : "NULL"));
            System.out.println("arretsField: " + (arretsField != null ? "OK" : "NULL"));
            System.out.println("distanceField: " + (distanceField != null ? "OK" : "NULL"));
            System.out.println("dureeField: " + (dureeField != null ? "OK" : "NULL"));
            System.out.println("ajouterButton: " + (ajouterButton != null ? "OK" : "NULL"));
            System.out.println("mapContainer: " + (mapContainer != null ? "OK" : "NULL"));

            // Initialisation des champs si nécessaire
            if (villeDepartField != null) villeDepartField.setPromptText("Entrez la ville de départ");
            if (villeArriveeField != null) villeArriveeField.setPromptText("Entrez la ville d'arrivée");
            if (arretsField != null) arretsField.setPromptText("Entrez les arrêts (séparés par des virgules)");
            if (distanceField != null) distanceField.setPromptText("Entrez la distance en km");
            if (dureeField != null) dureeField.setPromptText("Entrez la durée estimée");
            if (ajouterButton != null) ajouterButton.setText("Ajouter");
            
            // Initialiser la carte
            initializeMap();
            
            // Ajouter des écouteurs pour mettre à jour la carte lorsque les champs changent
            if (villeDepartField != null) {
                villeDepartField.textProperty().addListener((observable, oldValue, newValue) -> {
                    updateMapPreview();
                });
            }
            
            if (villeArriveeField != null) {
                villeArriveeField.textProperty().addListener((observable, oldValue, newValue) -> {
                    updateMapPreview();
                });
            }
            
            if (arretsField != null) {
                arretsField.textProperty().addListener((observable, oldValue, newValue) -> {
                    updateMapPreview();
                });
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeMap() {
        if (mapContainer != null) {
            // Créer un canvas pour la carte
            mapCanvas = new Canvas(400, 200);
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            
            // Dessiner le fond de carte initial
            drawEmptyMap(gc);
            
            // Ajouter le canvas au conteneur
            mapContainer.getChildren().clear();
            mapContainer.getChildren().add(mapCanvas);
        } else {
            System.err.println("Erreur: mapContainer est null - vérifiez que l'ID est correctement défini dans le FXML");
        }
    }

    private void drawEmptyMap(GraphicsContext gc) {
        // Effacer le canvas
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner le fond de la carte
        gc.setFill(Color.LIGHTBLUE.brighter());
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner une bordure
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, mapCanvas.getWidth() - 2, mapCanvas.getHeight() - 2);
        
        // Dessiner un message d'instruction
        gc.setFill(Color.DARKGRAY);
        gc.setFont(new Font("Arial", 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Entrez les villes pour voir l'aperçu de l'itinéraire", 
                    mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2);
    }

    private void updateMapPreview() {
        if (mapCanvas == null || villeDepartField == null || villeArriveeField == null) {
            return;
        }
        
        String villeDepart = villeDepartField.getText().trim();
        String villeArrivee = villeArriveeField.getText().trim();
        
        if (villeDepart.isEmpty() || villeArrivee.isEmpty()) {
            // Si les villes ne sont pas spécifiées, afficher la carte vide
            drawEmptyMap(mapCanvas.getGraphicsContext2D());
            return;
        }
        
        // Récupérer les arrêts
        List<String> arrets = new ArrayList<>();
        if (arretsField != null && !arretsField.getText().trim().isEmpty()) {
            arrets = Arrays.asList(arretsField.getText().split(","));
        }
        
        // Dessiner la carte avec l'itinéraire
        drawItineraireMap(mapCanvas.getGraphicsContext2D(), villeDepart, villeArrivee, arrets);
    }

    private void drawItineraireMap(GraphicsContext gc, String villeDepart, String villeArrivee, List<String> arrets) {
        // Effacer le canvas
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner le fond de la carte (simulé)
        gc.setFill(Color.LIGHTBLUE.brighter());
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Dessiner une bordure
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, mapCanvas.getWidth() - 2, mapCanvas.getHeight() - 2);
        
        // Calculer les positions des villes sur la carte
        double startX = 50;
        double endX = mapCanvas.getWidth() - 50;
        double y = mapCanvas.getHeight() / 2;
        
        // Dessiner la ligne d'itinéraire principale
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);
        gc.strokeLine(startX, y, endX, y);
        
        // Dessiner la ville de départ
        drawCity(gc, startX, y, villeDepart, true);
        
        // Dessiner la ville d'arrivée
        drawCity(gc, endX, y, villeArrivee, true);
        
        // Dessiner les arrêts intermédiaires
        if (!arrets.isEmpty()) {
            double segmentLength = (endX - startX) / (arrets.size() + 1);
            for (int i = 0; i < arrets.size(); i++) {
                double x = startX + segmentLength * (i + 1);
                drawCity(gc, x, y, arrets.get(i).trim(), false);
            }
        }
        
        // Dessiner le titre de la carte
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Aperçu de l'itinéraire", mapCanvas.getWidth() / 2, 20);
        
        // Dessiner la distance si disponible
        if (distanceField != null && !distanceField.getText().isEmpty()) {
            try {
                double distance = Double.parseDouble(distanceField.getText());
                gc.setFont(new Font("Arial", 12));
                gc.fillText(String.format("Distance: %.1f km", distance), 
                            mapCanvas.getWidth() / 2, mapCanvas.getHeight() - 20);
            } catch (NumberFormatException e) {
                // Ignorer si la distance n'est pas un nombre valide
            }
        }
        
        // Dessiner la durée si disponible
        if (dureeField != null && !dureeField.getText().isEmpty()) {
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Durée: " + dureeField.getText(), 
                        mapCanvas.getWidth() / 2, mapCanvas.getHeight() - 5);
        }
    }

    private void drawCity(GraphicsContext gc, double x, double y, String name, boolean isEndpoint) {
        // Dessiner un cercle pour la ville
        if (isEndpoint) {
            // Villes de départ/arrivée plus grandes et en rouge
            gc.setFill(Color.RED);
            gc.fillOval(x - 8, y - 8, 16, 16);
        } else {
            // Arrêts intermédiaires plus petits et en vert
            gc.setFill(Color.GREEN);
            gc.fillOval(x - 6, y - 6, 12, 12);
        }
        
        // Dessiner le nom de la ville
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 10));
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Alterner les positions des noms pour éviter les chevauchements
        if (isEndpoint) {
            gc.fillText(name, x, y - 15);
        } else {
            gc.fillText(name, x, y + 20);
        }
    }

    @FXML
    private void ajouterItineraire(ActionEvent event) {
        System.out.println("=== Tentative d'ajout d'un itinéraire ===");
        
        // Vérification que les champs sont initialisés
        if (villeDepartField == null || villeArriveeField == null || 
            arretsField == null || distanceField == null || dureeField == null) {
            System.err.println("Erreur: Certains champs sont null");
            showAlert("Erreur", "Erreur d'initialisation des champs", Alert.AlertType.ERROR);
            return;
        }

        // Validation des champs
        if (villeDepartField.getText().isEmpty() || villeArriveeField.getText().isEmpty() ||
            arretsField.getText().isEmpty() || distanceField.getText().isEmpty() || 
            dureeField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            double distance;
            try {
                distance = Double.parseDouble(distanceField.getText());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La distance doit être un nombre valide", Alert.AlertType.ERROR);
                return;
            }

            // Création d'un nouvel itinéraire
            Itineraire itineraire = new Itineraire();
            itineraire.setPointDepart(villeDepartField.getText());
            itineraire.setPointArrivee(villeArriveeField.getText());
            itineraire.setArrets(arretsField.getText());
            itineraire.setDistance(distance);
            itineraire.setDureeEstimee(dureeField.getText());
            
            // Ajout de l'itinéraire
            itineraireService.ajouter(itineraire);
            
            showAlert("Succès", "Itinéraire ajouté avec succès", Alert.AlertType.INFORMATION);
            
            // Notifier le changement de données
            tn.esprit.demo.util.DataRefreshManager.getInstance().notifyDataChanged("itineraire");
            
            // Retourner à la page de gestion
            simpleGoBack(event);
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthode simplifiée pour retourner à la page précédente
    private void simpleGoBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Ajoutez cette méthode pour gérer l'action du bouton "Retour"
    @FXML
    public void retour(ActionEvent event) {
        try {
            System.out.println("Retour à la liste des itinéraires depuis AjouterItineraireController");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void calculerItineraire(ActionEvent event) {
        String villeDepart = villeDepartField.getText().trim();
        String villeArrivee = villeArriveeField.getText().trim();
        
        if (villeDepart.isEmpty() || villeArrivee.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir les villes de départ et d'arrivée", Alert.AlertType.ERROR);
            return;
        }
        
        // Utiliser un thread séparé pour ne pas bloquer l'interface utilisateur
        Task<TransportAPI.ItineraireInfo> task = new Task<>() {
            @Override
            protected TransportAPI.ItineraireInfo call() throws Exception {
                return TransportAPI.getRouteInfo(villeDepart, villeArrivee);
            }
        };
        
        task.setOnSucceeded(e -> {
            TransportAPI.ItineraireInfo info = task.getValue();
            if (info.getDistance() > 0) {
                distanceField.setText(String.format("%.2f", info.getDistance()));
                dureeField.setText(info.getDuree());
                
                // Mettre à jour la carte avec les nouvelles informations
                updateMapPreview();
            } else {
                distanceField.setText("");
                dureeField.setText("");
                showAlert("Erreur", "Impossible de calculer l'itinéraire: " + info.getDuree(), Alert.AlertType.ERROR);
                
                // Réinitialiser la carte
                if (mapCanvas != null) {
                    drawEmptyMap(mapCanvas.getGraphicsContext2D());
                }
            }
        });
        
        task.setOnFailed(e -> {
            distanceField.setText("");
            dureeField.setText("");
            showAlert("Erreur", "Erreur lors du calcul de l'itinéraire", Alert.AlertType.ERROR);
            
            // Réinitialiser la carte
            if (mapCanvas != null) {
                drawEmptyMap(mapCanvas.getGraphicsContext2D());
            }
        });
        
        new Thread(task).start();
    }

    // Méthode pour estimer la durée en minutes à partir d'une chaîne de caractères
    private int estimateDurationInMinutes(String durationStr) {
        int minutes = 0;
        
        // Format attendu: "2h30min" ou "45min" ou "1h"
        durationStr = durationStr.toLowerCase().trim();
        
        if (durationStr.contains("h") && durationStr.contains("min")) {
            String[] parts = durationStr.split("h");
            int hours = Integer.parseInt(parts[0].trim());
            String minPart = parts[1].replace("min", "").trim();
            int mins = Integer.parseInt(minPart);
            minutes = hours * 60 + mins;
        } else if (durationStr.contains("h")) {
            String hourPart = durationStr.replace("h", "").trim();
            int hours = Integer.parseInt(hourPart);
            minutes = hours * 60;
        } else if (durationStr.contains("min")) {
            String minPart = durationStr.replace("min", "").trim();
            minutes = Integer.parseInt(minPart);
        }
        
        return minutes;
    }
}
