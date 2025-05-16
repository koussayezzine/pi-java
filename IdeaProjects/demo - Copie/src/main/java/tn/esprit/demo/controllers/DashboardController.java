package tn.esprit.demo.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class DashboardController {

    @FXML
    private Text welcomeText;

    @FXML
    private Text timeText;
    
    @FXML
    private Label weatherLabel;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox trafficContainer;
    
    @FXML
    private Label trafficStatusLabel;

    @FXML
    private PieChart busTypeChart;

    @FXML
    private StackPane mapContainer;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du DashboardController...");
        
        // Animation de fondu sur le texte de bienvenue
        if (welcomeText != null) {
            FadeTransition fadeInWelcome = new FadeTransition(Duration.seconds(2), welcomeText);
            fadeInWelcome.setFromValue(0.0);
            fadeInWelcome.setToValue(1.0);
            fadeInWelcome.play();
        } else {
            System.out.println("welcomeText est null");
        }

        // Affiche l'heure en temps réel
        if (timeText != null) {
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                timeText.setText("Heure: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Animation.INDEFINITE);
            clock.play();
        } else {
            System.out.println("timeText est null");
        }
        
        // Charger la météo au démarrage
        refreshWeather(null);
        
        // Charger les informations de trafic
        refreshTrafficInfo(null);
        
        // Charger la répartition des bus par type
        loadBusTypeChart();
        
        // Charger la carte de localisation des bus
        loadBusLocationMap();
    }
    
    @FXML
    private void refreshWeather(ActionEvent event) {
        if (weatherLabel == null) {
            System.out.println("weatherLabel est null");
            return;
        }
        
        weatherLabel.setText("Chargement...");
        
        Task<String> weatherTask = new Task<>() {
            @Override
            protected String call() {
                try {
                    // Simuler un délai réseau
                    Thread.sleep(1000);
                    
                    // Générer des données météo aléatoires
                    Random random = new Random();
                    int temperature = 20 + random.nextInt(15);
                    String[] conditions = {"Ensoleillé", "Partiellement nuageux", "Nuageux", "Pluvieux"};
                    String condition = conditions[random.nextInt(conditions.length)];
                    
                    return "Tunis: " + temperature + "°C, " + condition;
                } catch (Exception e) {
                    return "Erreur: " + e.getMessage();
                }
            }
        };
        
        weatherTask.setOnSucceeded(e -> {
            Platform.runLater(() -> weatherLabel.setText(weatherTask.getValue()));
        });
        
        weatherTask.setOnFailed(e -> {
            Platform.runLater(() -> weatherLabel.setText("Erreur de chargement"));
        });
        
        new Thread(weatherTask).start();
    }

    @FXML
    private void handleProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AfficherBus.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la page des bus: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleService(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/AfficherItineraires.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la page des itinéraires: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la page des itinéraires: " + e.getMessage(), Alert.AlertType.ERROR);
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
    private void handleDashboard(ActionEvent event) {
        // Déjà sur le dashboard, ne rien faire
        System.out.println("Déjà sur le dashboard");
    }

    @FXML
    private void refreshTrafficInfo(ActionEvent event) {
        if (trafficStatusLabel == null || trafficContainer == null) {
            System.out.println("trafficStatusLabel ou trafficContainer est null");
            return;
        }
        
        trafficStatusLabel.setText("Chargement des informations de trafic...");
        trafficContainer.getChildren().clear();
        
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                // Simuler un délai réseau
                Thread.sleep(1000);
                
                // Générer des données de trafic aléatoires
                return List.of(
                    "Avenue Habib Bourguiba: Trafic fluide",
                    "Avenue Mohamed V: Trafic modéré",
                    "Avenue de Carthage: Trafic dense"
                );
            }
        };
        
        task.setOnSucceeded(e -> {
            List<String> trafficInfos = task.getValue();
            
            Platform.runLater(() -> {
                if (trafficInfos.isEmpty()) {
                    trafficStatusLabel.setText("Aucune information de trafic disponible");
                    return;
                }
                
                for (String info : trafficInfos) {
                    HBox infoBox = new HBox(10);
                    infoBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Circle statusCircle = new Circle(5);
                    if (info.contains("fluide")) {
                        statusCircle.setFill(Color.GREEN);
                    } else if (info.contains("modéré")) {
                        statusCircle.setFill(Color.ORANGE);
                    } else {
                        statusCircle.setFill(Color.RED);
                    }
                    
                    Label infoLabel = new Label(info);
                    
                    infoBox.getChildren().addAll(statusCircle, infoLabel);
                    trafficContainer.getChildren().add(infoBox);
                }
                
                trafficStatusLabel.setText("Mis à jour à " + 
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                trafficStatusLabel.setText("Erreur lors du chargement des informations");
            });
        });
        
        new Thread(task).start();
    }

    private void loadBusTypeChart() {
        if (busTypeChart == null) {
            System.out.println("busTypeChart est null");
            return;
        }
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Standard", 15),
            new PieChart.Data("Confort", 8),
            new PieChart.Data("Premium", 12),
            new PieChart.Data("VIP", 5)
        );
        
        busTypeChart.setData(pieChartData);
        busTypeChart.setTitle("Types de bus");
        busTypeChart.setLabelsVisible(true);
        
        // Ajouter des tooltips
        pieChartData.forEach(data -> {
            Tooltip tooltip = new Tooltip(
                data.getName() + ": " + (int)data.getPieValue() + " bus"
            );
            Tooltip.install(data.getNode(), tooltip);
        });
    }

    private void loadBusLocationMap() {
        if (mapContainer == null) {
            System.out.println("mapContainer est null");
            return;
        }
        
        // Définir une taille réduite pour le conteneur
        mapContainer.setMinSize(250, 200);
        mapContainer.setPrefSize(250, 200);
        mapContainer.setMaxSize(250, 200);
        
        // Créer un canvas pour dessiner la carte avec la même taille
        Canvas canvas = new Canvas(250, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Dessiner le fond de la carte (simulé)
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Dessiner une grille simple pour simuler les rues
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(0.5);
        for (int i = 0; i < canvas.getWidth(); i += 25) {
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int i = 0; i < canvas.getHeight(); i += 25) {
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }
        
        // Dessiner le titre de la carte
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Carte de Tunis", canvas.getWidth() / 2, 15);
        
        // Dessiner quelques bus sur la carte
        Random random = new Random();
        Color[] colors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.RED};
        
        for (int i = 0; i < 10; i++) {
            double x = 25 + random.nextDouble() * (canvas.getWidth() - 50);
            double y = 30 + random.nextDouble() * (canvas.getHeight() - 60);
            
            gc.setFill(colors[random.nextInt(colors.length)]);
            gc.fillOval(x - 4, y - 4, 8, 8);
            
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 8));
            gc.fillText(String.valueOf(i + 1), x, y - 6);
        }
        
        // Ajouter le canvas au conteneur
        mapContainer.getChildren().clear();
        mapContainer.getChildren().add(canvas);
        
        // Ajouter une bordure au conteneur pour le rendre visible
        mapContainer.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/demo/Statistiques.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/demo/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage des statistiques: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'affichage des statistiques: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}