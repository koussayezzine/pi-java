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
import javafx.stage.Modality;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.concurrent.Task;
import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.TransportAPI;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierItineraireController implements Initializable {

    private ItineraireService itineraireService;
    private Itineraire itineraireToModify;

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
    private Button modifierButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation du contrôleur ModifierItineraireController ===");
        try {
            itineraireService = new ItineraireService();
            
            // Vérification détaillée des champs
            System.out.println("Vérification des champs FXML:");
            System.out.println("villeDepartField: " + (villeDepartField != null ? "OK" : "NULL"));
            System.out.println("villeArriveeField: " + (villeArriveeField != null ? "OK" : "NULL"));
            System.out.println("arretsField: " + (arretsField != null ? "OK" : "NULL"));
            System.out.println("distanceField: " + (distanceField != null ? "OK" : "NULL"));
            System.out.println("dureeField: " + (dureeField != null ? "OK" : "NULL"));
            System.out.println("modifierButton: " + (modifierButton != null ? "OK" : "NULL"));
            
            // Vérifier les fichiers FXML
            checkFxmlFiles();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour vérifier si le fichier FXML existe
    private void checkFxmlFiles() {
        String[] possiblePaths = {
            "/tn/esprit/demo/ModifierItineraire.fxml",
            "/tn/esprit/demo/modifieritineraire.fxml",
            "/tn/esprit/demo/AfficherItineraires.fxml",
            "/tn/esprit/demo/afficheritineraires.fxml"
        };
        
        System.out.println("=== Vérification des chemins FXML ===");
        for (String path : possiblePaths) {
            URL url = getClass().getResource(path);
            System.out.println(path + ": " + (url != null ? "TROUVÉ à " + url : "NON TROUVÉ"));
        }
    }

    public void setItineraireToModify(Itineraire itineraire) {
        System.out.println("=== Modification d'un itinéraire ===");
        this.itineraireToModify = itineraire;
        
        if (itineraire != null) {
            System.out.println("Itinéraire à modifier: ID=" + itineraire.getIdItin());
            
            // Remplir les champs avec les données de l'itinéraire
            if (villeDepartField != null) villeDepartField.setText(itineraire.getVilleDepart());
            if (villeArriveeField != null) villeArriveeField.setText(itineraire.getVilleArrivee());
            if (arretsField != null) arretsField.setText(itineraire.getArrets());
            if (distanceField != null) distanceField.setText(String.valueOf(itineraire.getDistance()));
            if (dureeField != null) dureeField.setText(itineraire.getDureeEstimee());
            
            // Changer le texte du bouton si nécessaire
            if (modifierButton != null) modifierButton.setText("Modifier");
        } else {
            System.err.println("Erreur: Itinéraire à modifier est null");
        }
    }

    @FXML
    private void modifierItineraire(ActionEvent event) {
        System.out.println("=== Tentative de modification d'un itinéraire ===");
        
        // Vérifier que l'itinéraire à modifier existe
        if (itineraireToModify == null) {
            showAlert("Erreur", "Aucun itinéraire à modifier", Alert.AlertType.ERROR);
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
            
            // Mettre à jour l'itinéraire
            itineraireToModify.setVilleDepart(villeDepartField.getText());
            itineraireToModify.setVilleArrivee(villeArriveeField.getText());
            itineraireToModify.setArrets(arretsField.getText());
            itineraireToModify.setDistance(distance);
            itineraireToModify.setDureeEstimee(dureeField.getText());

            // Sauvegarder les modifications
            itineraireService.modifier(itineraireToModify);
            System.out.println("Itinéraire modifié avec succès: ID=" + itineraireToModify.getIdItin());

            showAlert("Succès", "Itinéraire modifié avec succès", Alert.AlertType.INFORMATION);
            
            // Notifier le changement de données
            tn.esprit.demo.util.DataRefreshManager.getInstance().notifyDataChanged("itineraire");
            
            // Retourner à la page de gestion
            simpleGoBack(event);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour la distance", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification de l'itinéraire: " + e.getMessage(), Alert.AlertType.ERROR);
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

    @FXML
    public void goBack(ActionEvent event) {
        try {
            System.out.println("Retour à la liste des itinéraires depuis ModifierItineraireController");
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
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

    private void goBackAndRefresh(ActionEvent event) {
        try {
            // Notifier le changement de données avant de naviguer
            tn.esprit.demo.util.DataRefreshManager.getInstance().notifyDataChanged("itineraire");
            
            // Naviguer vers la page AfficherItineraires
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void afficherInfosTrafic(ActionEvent event) {
        String villeDepart = villeDepartField.getText().trim();
        String villeArrivee = villeArriveeField.getText().trim();
        
        if (villeDepart.isEmpty() || villeArrivee.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir les villes de départ et d'arrivée", Alert.AlertType.ERROR);
            return;
        }
        
        // Coordonnées approximatives pour les villes tunisiennes (à remplacer par des valeurs réelles)
        double lat = 36.8065;
        double lon = 10.1815;
        
        if (villeDepart.equalsIgnoreCase("sousse")) {
            lat = 35.8245;
            lon = 10.6346;
        } else if (villeDepart.equalsIgnoreCase("sfax")) {
            lat = 34.7406;
            lon = 10.7603;
        }
        
        final double latitude = lat;
        final double longitude = lon;
        
        Task<List<TransportAPI.TrafficInfo>> task = new Task<>() {
            @Override
            protected List<TransportAPI.TrafficInfo> call() throws Exception {
                return TransportAPI.getTrafficInfo(latitude, longitude);
            }
        };
        
        task.setOnSucceeded(e -> {
            List<TransportAPI.TrafficInfo> trafficInfos = task.getValue();
            
            // Créer une fenêtre pour afficher les informations de trafic
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            
            Label titleLabel = new Label("Informations de trafic pour " + villeDepart);
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            content.getChildren().add(titleLabel);
            
            for (TransportAPI.TrafficInfo info : trafficInfos) {
                HBox infoBox = new HBox(10);
                
                Label roadLabel = new Label(info.getRoadName() + ":");
                roadLabel.setMinWidth(200);
                
                String statusColor;
                if (info.getTrafficStatus().equals("Fluide")) {
                    statusColor = "green";
                } else if (info.getTrafficStatus().equals("Modéré")) {
                    statusColor = "orange";
                } else {
                    statusColor = "red";
                }
                
                Label statusLabel = new Label(info.getTrafficStatus() + " (" + info.getCongestion() + "%)");
                statusLabel.setStyle("-fx-text-fill: " + statusColor + ";");
                
                Label speedLabel = new Label(info.getCurrentSpeed() + " km/h");
                
                infoBox.getChildren().addAll(roadLabel, statusLabel, speedLabel);
                content.getChildren().add(infoBox);
            }
            
            Stage trafficStage = new Stage();
            trafficStage.setTitle("Informations de trafic");
            trafficStage.setScene(new Scene(content, 500, 400));
            trafficStage.initModality(Modality.APPLICATION_MODAL);
            trafficStage.show();
        });
        
        task.setOnFailed(e -> {
            showAlert("Erreur", "Erreur lors de la récupération des informations de trafic", Alert.AlertType.ERROR);
        });
        
        new Thread(task).start();
    }
}
