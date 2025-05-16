package tn.esprit.demo.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.DataRefreshManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SupprimerItineraireController implements Initializable {

    @FXML
    private TableView<Itineraire> itineraireTable;
    @FXML
    private TableColumn<Itineraire, String> villeDepartColumn;
    @FXML
    private TableColumn<Itineraire, String> villeArriveeColumn;
    @FXML
    private TableColumn<Itineraire, String> arretsColumn;
    @FXML
    private TableColumn<Itineraire, Double> distanceColumn;
    @FXML
    private TableColumn<Itineraire, String> dureeColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label villeDepartLabel;
    @FXML
    private Label villeArriveeLabel;
    @FXML
    private Label arretsLabel;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label statusLabel;

    private ItineraireService itineraireService;
    private FilteredList<Itineraire> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== Initialisation du contrôleur SupprimerItineraireController ===");
        try {
            itineraireService = new ItineraireService();
            
            // Configuration des colonnes
            if (villeDepartColumn != null) villeDepartColumn.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
            if (villeArriveeColumn != null) villeArriveeColumn.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
            if (arretsColumn != null) arretsColumn.setCellValueFactory(new PropertyValueFactory<>("arrets"));
            if (distanceColumn != null) distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
            if (dureeColumn != null) dureeColumn.setCellValueFactory(new PropertyValueFactory<>("dureeEstimee"));
            
            // Chargement des données
            loadData();
            
            // Configuration de la recherche
            setupSearch();
            
            // Configuration de la sélection
            if (itineraireTable != null) {
                itineraireTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateItineraireDetails(newSelection);
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            List<Itineraire> itineraires = itineraireService.recuperer();
            filteredList = new FilteredList<>(FXCollections.observableArrayList(itineraires), p -> true);
            itineraireTable.setItems(filteredList);
            System.out.println("=== Données chargées avec succès ===");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des itinéraires: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement des itinéraires : " + e.getMessage());
        }
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(itineraire -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (itineraire.getVilleDepart().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (itineraire.getVilleArrivee().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (itineraire.getArrets().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }
    
    private void updateItineraireDetails(Itineraire itineraire) {
        if (itineraire != null) {
            if (villeDepartLabel != null) villeDepartLabel.setText(itineraire.getVilleDepart());
            if (villeArriveeLabel != null) villeArriveeLabel.setText(itineraire.getVilleArrivee());
            if (arretsLabel != null) arretsLabel.setText(itineraire.getArrets());
            if (distanceLabel != null) distanceLabel.setText(String.valueOf(itineraire.getDistance()) + " km");
        }
    }

    @FXML
    private void supprimerItineraire(ActionEvent event) {
        Itineraire selectedItineraire = itineraireTable.getSelectionModel().getSelectedItem();
        if (selectedItineraire == null) {
            showSnackbar("⚠️ Veuillez sélectionner un itinéraire à supprimer.");
            return;
        }

        // Confirmation de suppression
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cet itinéraire ?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                // Supprimer l'itinéraire
                itineraireService.supprimer(selectedItineraire.getIdItin());
                System.out.println("Itinéraire supprimé avec succès, ID: " + selectedItineraire.getIdItin());
                
                // Afficher un message de succès
                showSnackbar("✅ Itinéraire supprimé avec succès !");
                
                // Notifier le changement de données
                DataRefreshManager.getInstance().notifyDataChanged("itineraire");
                
                // Retourner à la page de gestion
                simpleGoBack(event);
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void annuler() {
        itineraireTable.getSelectionModel().clearSelection();
        if (villeDepartLabel != null) villeDepartLabel.setText("--");
        if (villeArriveeLabel != null) villeArriveeLabel.setText("--");
        if (arretsLabel != null) arretsLabel.setText("--");
        if (distanceLabel != null) distanceLabel.setText("--");
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            System.out.println("Retour à la liste des itinéraires depuis SupprimerItineraireController");
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la navigation: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        // Cette méthode est appelée lorsque l'utilisateur clique sur le bouton de recherche
        // La recherche est déjà gérée par le listener dans setupSearch()
    }
    
    public void setItineraireToDelete(Itineraire itineraire) {
        System.out.println("setItineraireToDelete appelé avec itinéraire: " + (itineraire != null ? itineraire.getIdItin() : "null"));
        if (itineraire != null) {
            try {
                // Mettre à jour les détails de l'itinéraire de manière sécurisée
                Platform.runLater(() -> {
                    updateItineraireDetails(itineraire);
                    
                    // Sélectionner l'itinéraire dans le tableau après chargement des données
                    if (itineraireTable != null && itineraireTable.getItems() != null) {
                        itineraireTable.getItems().stream()
                            .filter(i -> i.getIdItin() == itineraire.getIdItin())
                            .findFirst()
                            .ifPresent(i -> itineraireTable.getSelectionModel().select(i));
                    }
                });
            } catch (Exception e) {
                System.err.println("Erreur dans setItineraireToDelete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showSnackbar(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> statusLabel.setVisible(false));
            pause.play();
        } else {
            // Fallback si le label n'est pas disponible
            System.out.println(message);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Nouvelle méthode simplifiée pour retourner à la page précédente
    private void simpleGoBack(ActionEvent event) {
        try {
            // Charger directement la page sans essayer d'accéder au contrôleur
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
            System.out.println("Navigation simple vers AfficherItineraires réussie");
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la navigation: " + e.getMessage());
        }
    }
}
