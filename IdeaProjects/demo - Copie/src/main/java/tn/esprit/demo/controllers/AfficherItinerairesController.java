package tn.esprit.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import tn.esprit.demo.models.Itineraire;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.DataRefreshManager;
import tn.esprit.demo.util.DataRefreshManager.DataRefreshListener;
import tn.esprit.demo.util.TransportAPI;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AfficherItinerairesController implements DataRefreshListener {

    @FXML
    private TableView<Itineraire> tableView;
    @FXML
    private TableColumn<Itineraire, Integer> idColumn;
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
    private TableColumn<Itineraire, String> dureeEstimeeColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane mapContainer;
    @FXML
    private Canvas mapView;

    private ItineraireService itineraireService;
    private FilteredList<Itineraire> filteredList;

    @FXML
    public void initialize() {
        // Initialiser le service
        itineraireService = new ItineraireService();
        
        System.out.println("=== Liste des ressources disponibles ===");
        // Vérifier les ressources disponibles
        String[] resources = {
            "/tn/esprit/demo/AjouterItineraire.fxml",
            "/tn/esprit/demo/ajouteritineraire.fxml",
            "/tn/esprit/demo/ModifierItineraire.fxml",
            "/tn/esprit/demo/modifieritineraire.fxml",
            "/tn/esprit/demo/SupprimerItineraire.fxml",
            "/tn/esprit/demo/supprimeritineraire.fxml",
            "/tn/esprit/demo/AfficherItineraires.fxml",
            "/tn/esprit/demo/afficheritineraires.fxml"
        };
        
        for (String res : resources) {
            System.out.println(res + ": " + (getClass().getResource(res) != null ? "TROUVÉ" : "NON TROUVÉ"));
        }
        
        // Configurer les colonnes de la table
        setupTableColumns();
        
        // Configurer la recherche
        setupSearch();
        
        // Charger les données
        refreshData();
        
        // S'enregistrer comme écouteur pour les mises à jour
        DataRefreshManager.getInstance().addListener(this);
        System.out.println("AfficherItinerairesController initialisé et enregistré comme listener");
        
        // Ajuster la taille de la carte pour qu'elle corresponde au conteneur parent
        // Ajuster la taille de la carte pour qu'elle corresponde au conteneur parent
        /*
        if (mapView != null) {
            mapView.prefWidthProperty().bind(mapContainer.widthProperty());
            mapView.prefHeightProperty().bind(mapContainer.heightProperty());
        }
        */
    }

    private void setupTableColumns() {
        try {
            // Supprimez ou commentez ces lignes qui configurent la colonne ID
            /*
            if (idColumn == null) {
                System.err.println("idColumn est null - vérifiez l'attribut fx:id dans le FXML");
                for (TableColumn<Itineraire, ?> column : itineraireTableView.getColumns()) {
                    if (column.getText().equals("ID")) {
                        idColumn = (TableColumn<Itineraire, Integer>) column;
                        break;
                    }
                }
            }
            
            if (idColumn != null) {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            }
            */
            
            // Continuez avec la configuration des autres colonnes
            if (villeDepartColumn != null) {
                villeDepartColumn.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
            }
            if (villeArriveeColumn != null) {
                villeArriveeColumn.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
            }
            if (arretsColumn != null) {
                arretsColumn.setCellValueFactory(new PropertyValueFactory<>("arrets"));
            }
            if (distanceColumn != null) {
                distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
            }
            if (dureeEstimeeColumn != null) {
                dureeEstimeeColumn.setCellValueFactory(new PropertyValueFactory<>("dureeEstimee"));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration des colonnes: " + e.getMessage());
            e.printStackTrace();
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

    @FXML
    public void refreshData() {
        System.out.println("Rafraîchissement des données...");
        try {
            List<Itineraire> itineraires = itineraireService.recuperer();
            System.out.println("Données récupérées: " + itineraires.size() + " itinéraires");
            
            // Vider et recréer la liste observable
            filteredList = new FilteredList<>(FXCollections.observableArrayList(itineraires), p -> true);
            tableView.setItems(filteredList);
            tableView.refresh(); // Force le rafraîchissement du tableau
            
            // Réappliquer le filtre de recherche si nécessaire
            if (searchField != null && searchField.getText() != null && !searchField.getText().isEmpty()) {
                setupSearch();
            }
            
            System.out.println("Données rafraîchies: " + itineraires.size() + " itinéraires");
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement des données: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showSnackbar(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setVisible(true);
            
            // Faire disparaître le message après 3 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> statusLabel.setVisible(false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
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
    public void goToAjouter(ActionEvent event) {
        try {
            System.out.println("Tentative de navigation vers AjouterItineraire.fxml");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AjouterItineraire.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur après le chargement si vous avez besoin de lui passer des données
            AjouterItineraireController controller = loader.getController();
            // controller.setDonnees(...); // Si vous avez besoin de passer des données
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers AjouterItineraire: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void goToModifier(ActionEvent event) {
        Itineraire selectedItineraire = tableView.getSelectionModel().getSelectedItem();
        if (selectedItineraire == null) {
            showAlert("Erreur", "Veuillez sélectionner un itinéraire à modifier", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            System.out.println("Tentative de navigation vers la page de modification");
            
            // Vérifier si le fichier FXML existe
            URL url = getClass().getResource("/tn/esprit/demo/ModifierItineraire.fxml");
            if (url == null) {
                System.err.println("Fichier FXML ModifierItineraire.fxml introuvable");
                showAlert("Erreur", "Fichier de modification introuvable", Alert.AlertType.ERROR);
                return;
            }
            System.out.println("Fichier FXML trouvé à: " + url);
            
            // Charger la page de modification
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            
            // Récupérer le contrôleur et lui passer l'itinéraire à modifier
            ModifierItineraireController controller = loader.getController();
            controller.setItineraireToModify(selectedItineraire);
            
            // Changer de scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers ModifierItineraire: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void goToSupprimer(ActionEvent event) {
        Itineraire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un itinéraire à supprimer", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/SupprimerItineraire.fxml"));
            Parent root = loader.load();
            SupprimerItineraireController controller = loader.getController();
            controller.setItineraireToDelete(selected);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers SupprimerItineraire: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void resetFilters(ActionEvent event) {
        if (searchField != null) {
            searchField.clear();
        }
        refreshData();
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/demo/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers Dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Utilisez ces méthodes pour vérifier les ressources disponibles
    private void listResources() {
        System.out.println("=== Liste des ressources disponibles ===");
        String[] resources = {
            "/tn/esprit/demo/AjouterItineraire.fxml",
            "/tn/esprit/demo/ajouteritineraire.fxml",
            "/tn/esprit/demo/ModifierItineraire.fxml",
            "/tn/esprit/demo/modifieritineraire.fxml",
            "/tn/esprit/demo/SupprimerItineraire.fxml",
            "/tn/esprit/demo/supprimeritineraire.fxml",
            "/tn/esprit/demo/AfficherItineraires.fxml",
            "/tn/esprit/demo/afficheritineraires.fxml"
        };
        
        for (String resource : resources) {
            System.out.println(resource + ": " + 
                (getClass().getResource(resource) != null ? "TROUVÉ" : "NON TROUVÉ"));
        }
    }
    
    // Implémentation de l'interface DataRefreshListener
    @Override
    public void onDataChanged(String dataType) {
        if ("itineraire".equals(dataType)) {
            System.out.println("Notification reçue: données itinéraire modifiées, rafraîchissement...");
            // Utilisez Platform.runLater pour éviter les problèmes de thread UI
            Platform.runLater(() -> {
                System.out.println("Exécution du rafraîchissement sur le thread JavaFX");
                refreshData();
            });
        }
    }
    
    // Se désinscrire quand le contrôleur n'est plus utilisé
    public void cleanup() {
        DataRefreshManager.getInstance().removeListener(this);
    }

    @FXML
    private void optimiserItineraires(ActionEvent event) {
        try {
            List<Itineraire> itineraires = itineraireService.recuperer();
            
            if (itineraires.isEmpty()) {
                showAlert("Information", "Aucun itinéraire à optimiser", Alert.AlertType.INFORMATION);
                return;
            }
            
            // Afficher un indicateur de progression
            ProgressIndicator progress = new ProgressIndicator();
            VBox progressBox = new VBox(10, new Label("Optimisation des itinéraires..."), progress);
            progressBox.setAlignment(Pos.CENTER);
            
            Stage progressStage = new Stage();
            progressStage.setScene(new Scene(progressBox, 300, 200));
            progressStage.setTitle("Optimisation en cours");
            progressStage.initModality(Modality.APPLICATION_MODAL);
            progressStage.show();
            
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    int total = itineraires.size();
                    int count = 0;
                    
                    for (Itineraire itineraire : itineraires) {
                        // Récupérer les informations de trafic pour cet itinéraire
                        TransportAPI.ItineraireInfo info = TransportAPI.getRouteInfo(
                                itineraire.getVilleDepart(), itineraire.getVilleArrivee());
                        
                        if (info.getDistance() > 0) {
                            // Mettre à jour l'itinéraire avec les nouvelles informations
                            itineraire.setDistance(info.getDistance());
                            itineraire.setDureeEstimee(info.getDuree());
                            itineraireService.modifier(itineraire);
                        }
                        
                        count++;
                        updateProgress(count, total);
                        Thread.sleep(500); // Pause pour éviter de surcharger l'API
                    }
                    
                    return null;
                }
            };
            
            progress.progressProperty().bind(task.progressProperty());
            
            task.setOnSucceeded(e -> {
                progressStage.close();
                refreshData();
                showAlert("Succès", "Itinéraires optimisés avec succès", Alert.AlertType.INFORMATION);
            });
            
            task.setOnFailed(e -> {
                progressStage.close();
                showAlert("Erreur", "Erreur lors de l'optimisation des itinéraires", Alert.AlertType.ERROR);
            });
            
            new Thread(task).start();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la récupération des itinéraires: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Gère la recherche d'itinéraires
     * @param event L'événement de clic sur le bouton de recherche
     */
    @FXML
    public void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            // Si le champ de recherche est vide, afficher tous les itinéraires
            refreshData(); // Utiliser refreshData() au lieu de refreshTable()
            return;
        }
        
        // Filtrer les itinéraires selon le terme de recherche
        FilteredList<Itineraire> filteredData = new FilteredList<>(tableView.getItems()); // Utiliser tableView au lieu de itineraireTable
        filteredData.setPredicate(itineraire -> {
            // Si le terme de recherche est vide, afficher tous les itinéraires
            if (searchTerm == null || searchTerm.isEmpty()) {
                return true;
            }
            
            // Comparer le terme de recherche avec les différentes propriétés de l'itinéraire
            String lowerCaseFilter = searchTerm.toLowerCase();
            
            if (itineraire.getVilleDepart().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Correspondance avec la ville de départ
            } else if (itineraire.getVilleArrivee().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Correspondance avec la ville d'arrivée
            } else if (itineraire.getArrets().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Correspondance avec les arrêts
            } else if (String.valueOf(itineraire.getDistance()).contains(lowerCaseFilter)) {
                return true; // Correspondance avec la distance
            }
            
            return false; // Pas de correspondance
        });
        
        // Mettre à jour la table avec les données filtrées
        SortedList<Itineraire> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty()); // Utiliser tableView au lieu de itineraireTable
        tableView.setItems(sortedData); // Utiliser tableView au lieu de itineraireTable
        
        // Afficher un message indiquant le nombre de résultats trouvés
        int resultCount = sortedData.size();
        if (resultCount == 0) {
            statusLabel.setText("Aucun itinéraire trouvé pour : " + searchTerm);
            statusLabel.setVisible(true);
        } else {
            statusLabel.setText(resultCount + " itinéraire(s) trouvé(s) pour : " + searchTerm);
            statusLabel.setVisible(true);
        }
    }
}
