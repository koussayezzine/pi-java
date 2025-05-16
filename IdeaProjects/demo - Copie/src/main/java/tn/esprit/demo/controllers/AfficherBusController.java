package tn.esprit.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tn.esprit.demo.models.Bus;
import tn.esprit.demo.services.BusService;
import tn.esprit.demo.services.ItineraireService;
import tn.esprit.demo.util.DataRefreshManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class AfficherBusController {

    @FXML
    private TableView<Bus> busTableView;

    @FXML
    private TableColumn<Bus, Integer> idBusColumn;

    @FXML
    private TableColumn<Bus, String> modeleColumn;

    @FXML
    private TableColumn<Bus, Integer> capaciteColumn;

    @FXML
    private TableColumn<Bus, String> plaqueImmatColumn;

    @FXML
    private TableColumn<Bus, Integer> idItineraireColumn;

    @FXML
    private TableColumn<Bus, String> typeColumn;

    @FXML
    private TableColumn<Bus, Double> tarifColumn;
    
    @FXML
    private TableColumn<Bus, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterTypeComboBox;

    @FXML
    private StackPane busMapContainer;

    private BusService busService;
    private ItineraireService itineraireService;
    private ObservableList<Bus> busList;
    private FilteredList<Bus> filteredList;
    private Canvas mapCanvas;
    private Timeline locationUpdater;
    private double[] busLatitudes;
    private double[] busLongitudes;

    @FXML
    public void initialize() {
        // Initialiser le service
        busService = new BusService();
        itineraireService = new ItineraireService();
        
        // Configurer les colonnes de la table
        setupTableColumns();
        
        // Initialiser la carte avec une taille fixe
        if (busMapContainer != null) {
            initializeMap();
        }
        
        // Charger les données
        refreshData();
        
        // S'abonner aux notifications de changement de données
        DataRefreshManager.getInstance().addListener("bus", event -> {
            Platform.runLater(() -> {
                refreshData();
            });
        });
    }

    @Override
    public void finalize() {
        // Se désabonner lors de la destruction du contrôleur
        DataRefreshManager.getInstance().removeListener("bus", null);
    }
    
    private void updateFilters() {
        if (filteredList == null) return;
        
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        String selectedType = filterTypeComboBox != null ? filterTypeComboBox.getValue() : null;
        
        filteredList.setPredicate(bus -> {
            boolean matchesSearch = true;
            boolean matchesType = true;
            
            // Filtre de recherche
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = bus.getModele().toLowerCase().contains(searchText) ||
                               bus.getPlaqueImmat().toLowerCase().contains(searchText) ||
                               String.valueOf(bus.getIdBus()).contains(searchText);
            }
            
            // Filtre par type
            if (selectedType != null && !selectedType.isEmpty()) {
                matchesType = bus.getType().equals(selectedType);
            }
            
            return matchesSearch && matchesType;
        });
        
        // Mettre à jour le nombre d'éléments filtrés
        System.out.println("Filtres appliqués: " + filteredList.size() + " bus affichés");
    }

    private void loadBusData() {
        try {
            List<Bus> buses = busService.recuperer();
            busList.clear();
            busList.addAll(buses);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des bus: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        updateFilters();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AjouterBus.fxml"));
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

    @FXML
    private void goToModifier(ActionEvent event) {
        Bus selectedBus = busTableView.getSelectionModel().getSelectedItem();
        if (selectedBus == null) {
            showAlert("Attention", "Veuillez sélectionner un bus à modifier", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/ModifierBus.fxml"));
            Parent root = loader.load();
            ModifierBusController controller = loader.getController();
            controller.setBus(selectedBus);
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

    @FXML
    private void goToSupprimer(ActionEvent event) {
        Bus selectedBus = busTableView.getSelectionModel().getSelectedItem();
        if (selectedBus == null) {
            showAlert("Attention", "Veuillez sélectionner un bus à supprimer", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/SupprimerBus.fxml"));
            Parent root = loader.load();
            SupprimerBusController controller = loader.getController();
            controller.setBusToDelete(selectedBus);
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

    @FXML
    private void handleRetour(ActionEvent event) {
        // Arrêter les mises à jour de localisation
        if (locationUpdater != null) {
            locationUpdater.stop();
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void resetFilters(ActionEvent event) {
        // Réinitialiser le champ de recherche
        searchField.clear();
        
        // Réinitialiser le ComboBox de filtre
        if (filterTypeComboBox != null) {
            filterTypeComboBox.setValue(null);
        }
        
        // Recharger toutes les données
        loadBusData();
        
        // Mettre à jour les filtres
        updateFilters();
    }
    
    private void initializeMap() {
        // Créer un canvas pour la carte avec une taille fixe
        mapCanvas = new Canvas(300, 300);
        
        // Créer un conteneur pour maintenir le canvas à une taille fixe
        StackPane mapHolder = new StackPane();
        mapHolder.getChildren().add(mapCanvas);
        mapHolder.setMaxSize(300, 300);
        mapHolder.setPrefSize(300, 300);
        mapHolder.setMinSize(300, 300);
        mapHolder.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
        
        // Ajouter le conteneur au busMapContainer
        busMapContainer.getChildren().clear();
        busMapContainer.getChildren().add(mapHolder);
        
        // Dessiner la carte initiale
        drawMap();
    }

    private void drawMap() {
        if (mapCanvas == null) return;
        
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        double width = mapCanvas.getWidth();
        double height = mapCanvas.getHeight();
        
        // Effacer le canvas
        gc.clearRect(0, 0, width, height);
        
        // Dessiner un fond de carte simple
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, width, height);
        
        // Dessiner une grille
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(0.5);
        for (int i = 0; i < width; i += 30) {
            gc.strokeLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 30) {
            gc.strokeLine(0, i, width, i);
        }
        
        // Dessiner les bus
        if (busList != null && !busList.isEmpty() && busLatitudes != null && busLongitudes != null) {
            for (int i = 0; i < busList.size() && i < busLatitudes.length; i++) {
                // Convertir les coordonnées géographiques en coordonnées d'écran
                double x = (busLongitudes[i] - 10.0) * width / 1.0;
                double y = height - (busLatitudes[i] - 36.0) * height / 1.5;
                
                // Limiter les coordonnées pour qu'elles restent dans le canvas
                x = Math.max(10, Math.min(width - 10, x));
                y = Math.max(10, Math.min(height - 10, y));
                
                // Choisir une couleur selon le type de bus
                Color busColor;
                String busType = busList.get(i).getType();
                if (busType == null) busType = "";
                
                switch (busType) {
                    case "Standard": busColor = Color.BLUE; break;
                    case "Confort": busColor = Color.GREEN; break;
                    case "Premium": busColor = Color.ORANGE; break;
                    case "VIP": busColor = Color.RED; break;
                    default: busColor = Color.GRAY;
                }
                
                // Dessiner un cercle pour représenter le bus
                gc.setFill(busColor);
                gc.fillOval(x - 5, y - 5, 10, 10);
                
                // Ajouter le numéro du bus
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(busList.get(i).getIdBus()), x, y - 8);
            }
        } else {
            // Afficher un message s'il n'y a pas de bus
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(14));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Aucun bus à afficher", width/2, height/2);
        }
        
        // Ajouter une légende
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Carte en temps réel des bus", 10, 20);
        
        // Ajouter une légende des types de bus
        double legendY = height - 60;
        gc.setFont(new Font(10));
        
        gc.setFill(Color.BLUE);
        gc.fillOval(10, legendY, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillText("Standard", 22, legendY + 8);
        
        gc.setFill(Color.GREEN);
        gc.fillOval(10, legendY + 15, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillText("Confort", 22, legendY + 23);
        
        gc.setFill(Color.ORANGE);
        gc.fillOval(80, legendY, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillText("Premium", 92, legendY + 8);
        
        gc.setFill(Color.RED);
        gc.fillOval(80, legendY + 15, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillText("VIP", 92, legendY + 23);
    }

    private void startLocationUpdates() {
        if (locationUpdater != null) {
            locationUpdater.stop();
        }
        
        // Mettre à jour les positions toutes les 2 secondes
        locationUpdater = new Timeline(
            new KeyFrame(Duration.seconds(2), event -> {
                updateBusLocations();
                drawMap();
            })
        );
        locationUpdater.setCycleCount(Timeline.INDEFINITE);
        locationUpdater.play();
    }

    private void initializeCoordinates() {
        // Vérifier si la liste des bus est vide
        if (busList == null || busList.isEmpty()) {
            System.out.println("Aucun bus à afficher sur la carte");
            return;
        }
        
        // Initialiser les coordonnées pour tous les bus
        int size = busList.size();
        busLatitudes = new double[size];
        busLongitudes = new double[size];
        
        // Générer des coordonnées aléatoires pour la démonstration
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            // Coordonnées pour la Tunisie (approximativement)
            busLatitudes[i] = 36.0 + random.nextDouble() * 1.5; // Entre 36.0 et 37.5
            busLongitudes[i] = 10.0 + random.nextDouble() * 1.0; // Entre 10.0 et 11.0
        }
        
        // Redessiner la carte
        drawMap();
    }

    private void updateBusLocations() {
        if (busLatitudes == null || busLongitudes == null) return;
        
        // Simuler le mouvement des bus
        for (int i = 0; i < busLatitudes.length; i++) {
            // Déplacer légèrement les bus
            busLatitudes[i] += (Math.random() - 0.5) * 0.01;
            busLongitudes[i] += (Math.random() - 0.5) * 0.01;
        }
    }

    private void highlightBusOnMap(Bus selectedBus) {
        if (mapCanvas == null) {
            System.out.println("Canvas non initialisé");
            return;
        }
        
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        
        // Effacer le canvas
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Redessiner la carte de base
        drawMap();
        
        // Trouver l'index du bus sélectionné
        int index = -1;
        for (int i = 0; i < busList.size(); i++) {
            if (busList.get(i).getIdBus() == selectedBus.getIdBus()) {
                index = i;
                break;
            }
        }
        
        if (index >= 0 && index < busLatitudes.length) {
            // Convertir les coordonnées en position sur le canvas
            double x = 20 + (busLongitudes[index] + 0.05) * (mapCanvas.getWidth() - 40);
            double y = 30 + (busLatitudes[index] + 0.05) * (mapCanvas.getHeight() - 60);
            
            // Dessiner un cercle plus grand pour le bus sélectionné
            gc.setFill(Color.RED);
            gc.fillOval(x - 8, y - 8, 16, 16);
            
            // Ajouter une info-bulle pour le bus sélectionné
            gc.setFill(Color.WHITE);
            gc.fillRect(x + 10, y - 30, 120, 60);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x + 10, y - 30, 120, 60);
            
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 10));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("Bus #" + selectedBus.getIdBus(), x + 15, y - 15);
            gc.fillText("Modèle: " + selectedBus.getModele(), x + 15, y);
            gc.fillText("Type: " + selectedBus.getType(), x + 15, y + 15);
            
            // Dessiner une ligne de connexion
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(x, y, x + 10, y - 10);
        }
    }

    @FXML
    private void refreshBusLocation(ActionEvent event) {
        Bus selectedBus = busTableView.getSelectionModel().getSelectedItem();
        if (selectedBus == null) {
            showAlert("Information", "Veuillez sélectionner un bus pour actualiser sa position", Alert.AlertType.INFORMATION);
            return;
        }
        
        // Trouver l'index du bus sélectionné
        int index = -1;
        for (int i = 0; i < busList.size(); i++) {
            if (busList.get(i).getIdBus() == selectedBus.getIdBus()) {
                index = i;
                break;
            }
        }
        
        if (index >= 0 && index < busLatitudes.length) {
            // Générer une nouvelle position pour le bus sélectionné
            busLatitudes[index] = 36.0 + Math.random() * 1.5; // Entre 36.0 et 37.5
            busLongitudes[index] = 10.0 + Math.random() * 1.0; // Entre 10.0 et 11.0
            
            // Redessiner la carte
            drawMap();
            
            // Mettre en évidence le bus sélectionné
            highlightBusOnMap(selectedBus);
            
            showAlert("Information", "Position du bus #" + selectedBus.getIdBus() + " actualisée", Alert.AlertType.INFORMATION);
        }
    }

    // Assurez-vous d'arrêter le Timeline lorsque le contrôleur est fermé
    public void shutdown() {
        if (locationUpdater != null) {
            locationUpdater.stop();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupTableColumns() {
        // Configurer les colonnes
        idBusColumn.setCellValueFactory(new PropertyValueFactory<>("idBus"));
        modeleColumn.setCellValueFactory(new PropertyValueFactory<>("modele"));
        capaciteColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        plaqueImmatColumn.setCellValueFactory(new PropertyValueFactory<>("plaqueImmat"));
        idItineraireColumn.setCellValueFactory(new PropertyValueFactory<>("idItineraire"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        tarifColumn.setCellValueFactory(new PropertyValueFactory<>("tarif"));
        
        // Configuration de la colonne de statut
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Personnaliser l'affichage du statut avec des couleurs
            statusColumn.setCellFactory(column -> new TableCell<Bus, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("Disponible".equals(item)) {
                            setStyle("-fx-text-fill: green;");
                        } else if ("En service".equals(item)) {
                            setStyle("-fx-text-fill: blue;");
                        } else if ("En maintenance".equals(item)) {
                            setStyle("-fx-text-fill: orange;");
                        } else if ("Hors service".equals(item)) {
                            setStyle("-fx-text-fill: red;");
                        }
                    }
                }
            });
        }
        
        // Ajouter un écouteur pour la sélection de bus pour mettre à jour la carte
        if (busTableView != null) {
            busTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    highlightBusOnMap(newSelection);
                }
            });
        }
        
        // Ajouter un écouteur pour le double-clic pour modifier
        busTableView.setRowFactory(tv -> {
            TableRow<Bus> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Bus bus = row.getItem();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/ModifierBus.fxml"));
                        Parent root = loader.load();
                        ModifierBusController controller = loader.getController();
                        controller.setBus(bus);
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
                        Stage stage = (Stage) busTableView.getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
            return row;
        });
    }

    private void refreshData() {
        try {
            System.out.println("Rafraîchissement des données...");
            
            // Initialiser les listes si elles sont nulles
            if (busList == null) {
                busList = FXCollections.observableArrayList();
            }
            
            // Récupérer les données
            List<Bus> buses = busService.recuperer();
            System.out.println("Buses récupérés: " + buses.size());
            
            // Mettre à jour la liste
            busList.clear();
            busList.addAll(buses);
            
            // Initialiser la liste filtrée si elle est nulle
            if (filteredList == null) {
                filteredList = new FilteredList<>(busList, p -> true);
                busTableView.setItems(filteredList);
            } else {
                // Forcer la mise à jour de la vue
                filteredList.setPredicate(p -> true);
            }
            
            // Mettre à jour les filtres
            updateFilters();
            
            // Initialiser la carte si elle n'est pas déjà initialisée
            if (mapCanvas == null && busMapContainer != null) {
                initializeMap();
            }
            
            // Initialiser les coordonnées si nécessaire
            if (busList.size() > 0 && (busLatitudes == null || busLongitudes == null || busLatitudes.length != busList.size())) {
                initializeCoordinates();
            } else if (busList.isEmpty()) {
                // S'il n'y a pas de bus, dessiner une carte vide
                if (mapCanvas != null) {
                    drawMap();
                }
            }
            
            // Démarrer les mises à jour de localisation si ce n'est pas déjà fait et s'il y a des bus
            if (locationUpdater == null && !busList.isEmpty()) {
                startLocationUpdates();
            } else if (busList.isEmpty() && locationUpdater != null) {
                // Arrêter les mises à jour s'il n'y a pas de bus
                locationUpdater.stop();
                locationUpdater = null;
            }
            
            System.out.println("Données rafraîchies: " + buses.size() + " bus");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des bus: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
