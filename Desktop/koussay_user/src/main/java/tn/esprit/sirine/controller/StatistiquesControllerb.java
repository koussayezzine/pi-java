package tn.esprit.sirine.controller;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tn.esprit.sirine.services.BusService;
import tn.esprit.sirine.services.ItineraireService;
import tn.esprit.sirine.utils.StatistiquesAPI;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

public class StatistiquesControllerb {

    @FXML private ComboBox<String> periodeComboBox;
    @FXML private ComboBox<String> typeBusComboBox;
    @FXML private ComboBox<String> itineraireComboBox;
    @FXML private ComboBox<String> comparisonPeriodeComboBox;

    @FXML private Label tauxOccupationLabel;
    @FXML private ProgressBar tauxOccupationProgress;
    @FXML private Label tauxOccupationEvolution;

    @FXML private Label ponctualiteLabel;
    @FXML private ProgressBar ponctualiteProgress;
    @FXML private Label ponctualiteEvolution;

    @FXML private Label consommationLabel;
    @FXML private ProgressBar consommationProgress;
    @FXML private Label consommationEvolution;

    @FXML private Label satisfactionLabel;
    @FXML private HBox starsContainer;
    @FXML private Label satisfactionEvolution;

    @FXML private LineChart<String, Number> evolutionChart;
    @FXML private WebView heatmapView;
    @FXML private PieChart typeBusChart;
    @FXML private PieChart itineraireChart;
    @FXML private BarChart<String, Number> incidentsChart;
    @FXML private LineChart<String, Number> comparativeChart;
    @FXML private VBox mainContainer;
    @FXML private HBox comparisonControls;
    @FXML
    private BarChart<String, Number> occupationChart;
    @FXML
    private VBox recommendationsContainer;
    @FXML
    private VBox chartsContainer;

    private BusService busService;
    private ItineraireService itineraireService;
    private Random random = new Random();
    private Stage loadingStage;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du StatistiquesController...");

        // Vérifiez si les éléments FXML sont correctement injectés
        if (occupationChart == null) {
            System.out.println("ERREUR: occupationChart est null! Vérifiez l'ID dans le fichier FXML.");
        } else {
            System.out.println("occupationChart correctement injecté.");
        }

        // Initialiser les services
        busService = new BusService();
        itineraireService = new ItineraireService();

        // Configuration des ComboBox
        initializeComboBoxes();

        // Initialiser les graphiques avec des valeurs par défaut
        initializeCharts();

        // Charger les données
        loadData();
    }

    private void initializeComboBoxes() {
        System.out.println("Initialisation des ComboBox...");

        // Initialiser la ComboBox de période
        if (periodeComboBox != null) {
            ObservableList<String> periodes = FXCollections.observableArrayList(
                "Aujourd'hui",
                "Cette semaine",
                "Ce mois",
                "Ce trimestre",
                "Cette année"
            );
            periodeComboBox.setItems(periodes);
            periodeComboBox.setValue("Ce mois"); // Valeur par défaut

            // Ajouter un écouteur pour recharger les données quand la période change
            periodeComboBox.setOnAction(event -> {
                loadData();
            });

            System.out.println("ComboBox période initialisée avec " + periodes.size() + " éléments");
        } else {
            System.out.println("ERREUR: periodeComboBox est null!");
        }

        // Initialiser la ComboBox de type de bus
        if (typeBusComboBox != null) {
            ObservableList<String> typesBus = FXCollections.observableArrayList(
                "Tous",
                "Standard",
                "Confort",
                "Premium",
                "VIP"
            );
            typeBusComboBox.setItems(typesBus);
            typeBusComboBox.setValue("Tous"); // Valeur par défaut

            // Ajouter un écouteur pour recharger les données quand le type change
            typeBusComboBox.setOnAction(event -> {
                loadData();
            });

            System.out.println("ComboBox type de bus initialisée avec " + typesBus.size() + " éléments");
        } else {
            System.out.println("ERREUR: typeBusComboBox est null!");
        }

        // Initialiser la ComboBox d'itinéraire
        if (itineraireComboBox != null) {
            try {
                // Récupérer les itinéraires depuis le service
                List<String> itineraires = new ArrayList<>();
                itineraires.add("Tous"); // Option par défaut

                // Ajouter les itinéraires depuis le service
                if (itineraireService != null) {
                    itineraireService.recuperer().forEach(itineraire -> {
                        itineraires.add(itineraire.getVilleDepart() + " - " + itineraire.getVilleArrivee());
                    });
                } else {
                    // Ajouter des itinéraires par défaut si le service n'est pas disponible
                    itineraires.add("Tunis - Sousse");
                    itineraires.add("Tunis - Sfax");
                    itineraires.add("Sousse - Monastir");
                    itineraires.add("Sfax - Gabès");
                }

                itineraireComboBox.setItems(FXCollections.observableArrayList(itineraires));
                itineraireComboBox.setValue("Tous"); // Valeur par défaut

                // Ajouter un écouteur pour recharger les données quand l'itinéraire change
                itineraireComboBox.setOnAction(event -> {
                    loadData();
                });

                System.out.println("ComboBox itinéraire initialisée avec " + itineraires.size() + " éléments");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation de la ComboBox d'itinéraire: " + e.getMessage());
                e.printStackTrace();

                // Initialiser avec des valeurs par défaut en cas d'erreur
                ObservableList<String> defaultItineraires = FXCollections.observableArrayList(
                    "Tous",
                    "Tunis - Sousse",
                    "Tunis - Sfax",
                    "Sousse - Monastir",
                    "Sfax - Gabès"
                );
                itineraireComboBox.setItems(defaultItineraires);
                itineraireComboBox.setValue("Tous");
            }
        } else {
            System.out.println("ERREUR: itineraireComboBox est null!");
        }

        // Initialiser la ComboBox de période de comparaison
        if (comparisonPeriodeComboBox != null) {
            ObservableList<String> periodes = FXCollections.observableArrayList(
                "Mois précédent",
                "Trimestre précédent",
                "Année précédente",
                "Même période l'an dernier"
            );
            comparisonPeriodeComboBox.setItems(periodes);
            comparisonPeriodeComboBox.setValue("Mois précédent"); // Valeur par défaut

            System.out.println("ComboBox période de comparaison initialisée avec " + periodes.size() + " éléments");
        } else {
            System.out.println("ERREUR: comparisonPeriodeComboBox est null!");
        }
    }

    private void initializeCharts() {
        // S'assurer que les graphiques sont correctement initialisés
        if (evolutionChart != null) {
            evolutionChart.setAnimated(false); // Désactiver l'animation pour éviter les problèmes
            evolutionChart.getData().clear();
        }

        if (occupationChart != null) {
            occupationChart.setAnimated(false);
            occupationChart.getData().clear();
        }

        if (typeBusChart != null) {
            typeBusChart.setData(FXCollections.observableArrayList());
        }

        if (itineraireChart != null) {
            itineraireChart.setData(FXCollections.observableArrayList());
        }
    }

    private void loadData() {
        System.out.println("Chargement des données pour les graphiques...");
        showLoadingIndicator(true);

        // Utiliser Platform.runLater pour éviter les problèmes de thread JavaFX
        Platform.runLater(() -> {
            try {
                // Données pour le graphique d'évolution
                if (evolutionChart != null) {
                    System.out.println("Configuration du graphique d'évolution...");
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Passagers");

                    // Ajouter des données de test
                    series.getData().add(new XYChart.Data<>("Jan", 150));
                    series.getData().add(new XYChart.Data<>("Fév", 200));
                    series.getData().add(new XYChart.Data<>("Mar", 180));
                    series.getData().add(new XYChart.Data<>("Avr", 250));
                    series.getData().add(new XYChart.Data<>("Mai", 300));
                    series.getData().add(new XYChart.Data<>("Juin", 280));

                    evolutionChart.getData().clear();
                    evolutionChart.getData().add(series);
                    System.out.println("Données ajoutées au graphique d'évolution: " + series.getData().size() + " points");
                } else {
                    System.out.println("ERREUR: evolutionChart est null!");
                }

                // Données pour le graphique d'occupation
                if (occupationChart != null) {
                    System.out.println("Configuration du graphique d'occupation...");
                    XYChart.Series<String, Number> occSeries = new XYChart.Series<>();
                    occSeries.setName("Taux d'occupation");
                    occSeries.getData().add(new XYChart.Data<>("Jan", 65));
                    occSeries.getData().add(new XYChart.Data<>("Fév", 70));
                    occSeries.getData().add(new XYChart.Data<>("Mar", 75));
                    occSeries.getData().add(new XYChart.Data<>("Avr", 80));

                    occupationChart.getData().clear();
                    occupationChart.getData().add(occSeries);
                    System.out.println("Données ajoutées au graphique d'occupation: " + occSeries.getData().size() + " points");
                } else {
                    System.out.println("ERREUR: occupationChart est null! Vérifiez l'ID dans le fichier FXML.");
                }

                // Données pour les graphiques circulaires
                if (typeBusChart != null) {
                    System.out.println("Configuration du graphique de types de bus...");
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("Standard", 50),
                        new PieChart.Data("Confort", 30),
                        new PieChart.Data("Premium", 15),
                        new PieChart.Data("VIP", 5)
                    );
                    typeBusChart.setData(pieChartData);
                    System.out.println("Données ajoutées au graphique circulaire: " + pieChartData.size() + " segments");
                } else {
                    System.out.println("ERREUR: typeBusChart est null!");
                }

                if (itineraireChart != null) {
                    System.out.println("Configuration du graphique d'itinéraires...");
                    ObservableList<PieChart.Data> itineraireData = FXCollections.observableArrayList(
                        new PieChart.Data("Tunis-Sousse", 40),
                        new PieChart.Data("Tunis-Sfax", 25),
                        new PieChart.Data("Sousse-Monastir", 20),
                        new PieChart.Data("Sfax-Gabès", 15)
                    );
                    itineraireChart.setData(itineraireData);
                    System.out.println("Données ajoutées au graphique d'itinéraires: " + itineraireData.size() + " segments");
                } else {
                    System.out.println("ERREUR: itineraireChart est null!");
                }

                // Mettre à jour les KPIs
                updateKPIs();

                showLoadingIndicator(false);
                System.out.println("Chargement des données terminé avec succès");
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des données: " + e.getMessage());
                e.printStackTrace();
                showLoadingIndicator(false);
                showAlert("Erreur", "Erreur lors du chargement des données: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void updateKPIs() {
        // Mettre à jour les indicateurs de performance clés
        if (tauxOccupationLabel != null) {
            tauxOccupationLabel.setText("75%");
            if (tauxOccupationProgress != null) {
                tauxOccupationProgress.setProgress(0.75);
            }
            if (tauxOccupationEvolution != null) {
                tauxOccupationEvolution.setText("+5% vs période précédente");
            }
        }

        if (ponctualiteLabel != null) {
            ponctualiteLabel.setText("92%");
            if (ponctualiteProgress != null) {
                ponctualiteProgress.setProgress(0.92);
            }
            if (ponctualiteEvolution != null) {
                ponctualiteEvolution.setText("+2% vs période précédente");
            }
        }
    }

    @FXML
    private void refreshData(ActionEvent event) {
        loadData();
    }

    @FXML
    private void retourDashboard(ActionEvent event) {
        try {
            System.out.println("Navigation vers le Dashboard...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/DashboardW.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/styleW.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            System.out.println("Navigation vers le Dashboard réussie");
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation vers le Dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers le Dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Affiche une boîte de dialogue pour personnaliser les graphiques
     * @param event L'événement déclencheur
     */
    @FXML
    public void showCustomizeDialog(ActionEvent event) {
        try {
            // Créer une boîte de dialogue
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Personnaliser les graphiques");
            dialog.setHeaderText("Options de personnalisation");

            // Créer le contenu de la boîte de dialogue
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Options pour les couleurs
            ColorPicker colorPicker1 = new ColorPicker(Color.DODGERBLUE);
            ColorPicker colorPicker2 = new ColorPicker(Color.ORANGERED); // Correction ici
            ColorPicker colorPicker3 = new ColorPicker(Color.LIMEGREEN);
            ColorPicker colorPicker4 = new ColorPicker(Color.GOLD);

            // Options pour les types de graphiques
            ToggleGroup chartTypeGroup = new ToggleGroup();
            RadioButton lineChartRadio = new RadioButton("Graphique linéaire");
            RadioButton barChartRadio = new RadioButton("Graphique à barres");
            RadioButton areaChartRadio = new RadioButton("Graphique de zone");
            lineChartRadio.setToggleGroup(chartTypeGroup);
            barChartRadio.setToggleGroup(chartTypeGroup);
            areaChartRadio.setToggleGroup(chartTypeGroup);
            lineChartRadio.setSelected(true);

            // Options pour l'animation
            CheckBox animationCheckBox = new CheckBox("Activer les animations");
            animationCheckBox.setSelected(true);

            // Options pour les symboles (points sur les lignes)
            CheckBox symbolsCheckBox = new CheckBox("Afficher les symboles");
            symbolsCheckBox.setSelected(true);

            // Ajouter les contrôles à la grille
            grid.add(new Label("Couleur série 1:"), 0, 0);
            grid.add(colorPicker1, 1, 0);
            grid.add(new Label("Couleur série 2:"), 0, 1);
            grid.add(colorPicker2, 1, 1);
            grid.add(new Label("Couleur série 3:"), 0, 2);
            grid.add(colorPicker3, 1, 2);
            grid.add(new Label("Couleur série 4:"), 0, 3);
            grid.add(colorPicker4, 1, 3);

            grid.add(new Separator(), 0, 4, 2, 1);

            grid.add(new Label("Type de graphique:"), 0, 5);
            grid.add(lineChartRadio, 1, 5);
            grid.add(barChartRadio, 1, 6);
            grid.add(areaChartRadio, 1, 7);

            grid.add(new Separator(), 0, 8, 2, 1);

            grid.add(animationCheckBox, 0, 9, 2, 1);
            grid.add(symbolsCheckBox, 0, 10, 2, 1);

            dialog.getDialogPane().setContent(grid);

            // Ajouter les boutons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Afficher la boîte de dialogue et attendre la réponse
            Optional<ButtonType> result = dialog.showAndWait();

            // Traiter la réponse
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Appliquer les personnalisations
                boolean useAnimation = animationCheckBox.isSelected();
                boolean showSymbols = symbolsCheckBox.isSelected();

                // Appliquer les couleurs personnalisées
                String style = String.format(
                    ".default-color0.chart-series-line { -fx-stroke: %s; }\n" +
                    ".default-color1.chart-series-line { -fx-stroke: %s; }\n" +
                    ".default-color2.chart-series-line { -fx-stroke: %s; }\n" +
                    ".default-color3.chart-series-line { -fx-stroke: %s; }\n" +
                    ".default-color0.chart-line-symbol { -fx-background-color: %s, white; }\n" +
                    ".default-color1.chart-line-symbol { -fx-background-color: %s, white; }\n" +
                    ".default-color2.chart-line-symbol { -fx-background-color: %s, white; }\n" +
                    ".default-color3.chart-line-symbol { -fx-background-color: %s, white; }",
                    toRGBCode(colorPicker1.getValue()),
                    toRGBCode(colorPicker2.getValue()),
                    toRGBCode(colorPicker3.getValue()),
                    toRGBCode(colorPicker4.getValue()),
                    toRGBCode(colorPicker1.getValue()),
                    toRGBCode(colorPicker2.getValue()),
                    toRGBCode(colorPicker3.getValue()),
                    toRGBCode(colorPicker4.getValue())
                );

                // Appliquer le style personnalisé
                if (occupationChart != null) {
                    occupationChart.getStylesheets().clear();
                    occupationChart.setStyle(style);
                    occupationChart.setAnimated(useAnimation);

                    // Ne faites aucune vérification de type pour occupationChart
                    // Supprimez complètement ces lignes:
                    // if (occupationChart instanceof LineChart) {
                    //     ((LineChart<?, ?>) occupationChart).setCreateSymbols(showSymbols);
                    // } else if (occupationChart instanceof BarChart) {
                    //     // Traitement spécifique pour BarChart si nécessaire
                    //     System.out.println("occupationChart est un BarChart, pas de symboles à configurer");
                    // }
                }

                if (comparativeChart != null) {
                    comparativeChart.getStylesheets().clear();
                    comparativeChart.setStyle(style);
                    comparativeChart.setAnimated(useAnimation);

                    // Vérifier le type avant d'appliquer des propriétés spécifiques
                    if (comparativeChart instanceof LineChart) {
                        ((LineChart<?, ?>) comparativeChart).setCreateSymbols(showSymbols);
                    }
                }

                // Changer le type de graphique si nécessaire
                if (lineChartRadio.isSelected()) {
                    // Déjà un LineChart, rien à faire
                } else if (barChartRadio.isSelected()) {
                    showNotification("Changement de type", "Changement vers graphique à barres non implémenté");

                    // Exemple d'approche alternative: créer une nouvelle série avec le même style
                    // mais ne pas essayer de convertir le graphique lui-même
                    if (occupationChart != null) {
                        // Appliquer le style au graphique existant
                        occupationChart.setStyle(style);
                        occupationChart.setAnimated(useAnimation);
                    }

                    if (comparativeChart != null) {
                        // Appliquer le style au graphique existant
                        comparativeChart.setStyle(style);
                        comparativeChart.setAnimated(useAnimation);
                    }
                } else if (areaChartRadio.isSelected()) {
                    showNotification("Changement de type", "Changement vers graphique de zone non implémenté");
                }

                showNotification("Personnalisation", "Les personnalisations ont été appliquées avec succès");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Erreur", "Erreur lors de la personnalisation: " + e.getMessage());
        }
    }

    /**
     * Convertit une couleur JavaFX en code RGB hexadécimal
     * @param color La couleur à convertir
     * @return Le code RGB hexadécimal
     */
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Utiliser show() au lieu de showAndWait() pour éviter le blocage pendant l'animation
        alert.show();
    }

    private void showNotification(String title, String message) {
        // Méthode simplifiée pour afficher une notification
        showAlert("Notification: " + title, message, Alert.AlertType.INFORMATION);
    }

    private void showLoadingIndicator(boolean show) {
        // Méthode simplifiée pour afficher/masquer un indicateur de chargement
        if (show) {
            System.out.println("Affichage de l'indicateur de chargement...");
        } else {
            System.out.println("Masquage de l'indicateur de chargement...");
        }
    }

    @FXML
    private void applyFilters(ActionEvent event) {
        // Récupérer les valeurs sélectionnées
        String periode = periodeComboBox.getValue();
        String typeBus = typeBusComboBox.getValue();
        String itineraire = itineraireComboBox.getValue();

        System.out.println("Application des filtres: Période=" + periode +
                          ", Type de bus=" + typeBus +
                          ", Itinéraire=" + itineraire);

        // Animation de transition pendant le chargement
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.5);

        fadeOut.setOnFinished(e -> {
            // Mettre à jour les graphiques avec les nouvelles données filtrées
            updateCharts(periode, typeBus, itineraire);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), mainContainer);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void updateCharts(String periode, String typeBus, String itineraire) {
        // Afficher un message de chargement
        showLoadingIndicator(true);

        // Utiliser un thread séparé pour ne pas bloquer l'interface
        Task<Map<String, Object>> task = new Task<>() {
            @Override
            protected Map<String, Object> call() {
                // Récupérer les données filtrées via l'API
                return StatistiquesAPI.getStatistiques(periode, typeBus, itineraire);
            }
        };

        task.setOnSucceeded(e -> {
            Map<String, Object> data = task.getValue();

            // Mettre à jour les graphiques avec les nouvelles données
            updateChartsWithData(data);

            // Masquer le message de chargement
            showLoadingIndicator(false);
        });

        new Thread(task).start();
    }

    private void updateChartsWithData(Map<String, Object> data) {
        // Mettre à jour les KPIs
        updateKPIs(data);

        // Mettre à jour le graphique d'évolution
        if (evolutionChart != null) {
            updateEvolutionChart(data);
        }

        // Mettre à jour le graphique d'occupation
        if (occupationChart != null) {
            updateOccupationChart(data);
        }

        // Mettre à jour les graphiques circulaires
        if (typeBusChart != null) {
            updateTypeBusChart(data);
        }

        if (itineraireChart != null) {
            updateItineraireChart(data);
        }

        // Mettre à jour les autres graphiques si nécessaire
    }

    @FXML
    private void afficherComparaison(ActionEvent event) {
        // Méthode simplifiée pour afficher la comparaison
        showAlert("Comparaison", "Affichage des données comparatives...", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void applyComparison(ActionEvent event) {
        // Récupérer les valeurs des filtres
        String periode = periodeComboBox.getValue();
        String comparisonPeriode = comparisonPeriodeComboBox.getValue();
        String typeBus = typeBusComboBox.getValue();
        String itineraire = itineraireComboBox.getValue();

        System.out.println("Application de la comparaison: " + periode + " vs " + comparisonPeriode);

        // Charger les données comparatives
        Task<Map<String, Object>> task = new Task<>() {
            @Override
            protected Map<String, Object> call() {
                return StatistiquesAPI.getComparativeData(
                    periode,
                    comparisonPeriode,
                    typeBus,
                    itineraire
                );
            }
        };

        task.setOnSucceeded(e -> {
            updateComparativeCharts(task.getValue());
        });

        new Thread(task).start();
    }

    /**
     * Met à jour les graphiques avec les données comparatives
     * @param data Les données comparatives reçues de l'API
     */
    private void updateComparativeCharts(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("Aucune donnée comparative reçue");
            return;
        }

        // Récupérer les noms des périodes
        String nomPeriode1 = (String) data.get("nomPeriode1");
        String nomPeriode2 = (String) data.get("nomPeriode2");

        // Récupérer les données mensuelles
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> donneesMensuelles = (List<Map<String, Object>>) data.get("donneesMensuelles");

        if (donneesMensuelles == null || donneesMensuelles.isEmpty()) {
            System.out.println("Aucune donnée mensuelle disponible pour la comparaison");
            return;
        }

        // Mettre à jour le graphique comparatif
        if (comparativeChart != null) {
            // Créer les séries pour chaque métrique et période
            XYChart.Series<String, Number> occupation1Series = new XYChart.Series<>();
            occupation1Series.setName("Occupation - " + nomPeriode1);

            XYChart.Series<String, Number> occupation2Series = new XYChart.Series<>();
            occupation2Series.setName("Occupation - " + nomPeriode2);

            // Ajouter les données à chaque série
            for (Map<String, Object> mois : donneesMensuelles) {
                String nomMois = (String) mois.get("mois");
                Number occupation1 = (Number) mois.get("occupation1");
                Number occupation2 = (Number) mois.get("occupation2");

                if (nomMois != null && occupation1 != null) {
                    occupation1Series.getData().add(new XYChart.Data<>(nomMois, occupation1));
                }

                if (nomMois != null && occupation2 != null) {
                    occupation2Series.getData().add(new XYChart.Data<>(nomMois, occupation2));
                }
            }

            // Animation de transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), comparativeChart);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.3);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), comparativeChart);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);

            fadeOut.setOnFinished(e -> {
                // Effacer les données existantes
                comparativeChart.getData().clear();

                // Ajouter les nouvelles séries
                comparativeChart.getData().addAll(occupation1Series, occupation2Series);

                // Ajouter des tooltips et des animations
                for (XYChart.Series<String, Number> series : comparativeChart.getData()) {
                    for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                        Tooltip tooltip = new Tooltip(
                            series.getName() + " - " + dataPoint.getXValue() + ": " +
                            String.format("%.1f%%", dataPoint.getYValue().doubleValue())
                        );

                        // Attendre que le nœud soit créé
                        Platform.runLater(() -> {
                            if (dataPoint.getNode() != null) {
                                Tooltip.install(dataPoint.getNode(), tooltip);

                                // Animation au survol
                                dataPoint.getNode().setOnMouseEntered(event -> {
                                    dataPoint.getNode().setScaleX(1.5);
                                    dataPoint.getNode().setScaleY(1.5);
                                    dataPoint.getNode().setEffect(new Glow(0.5));
                                });

                                dataPoint.getNode().setOnMouseExited(event -> {
                                    dataPoint.getNode().setScaleX(1.0);
                                    dataPoint.getNode().setScaleY(1.0);
                                    dataPoint.getNode().setEffect(null);
                                });
                            }
                        });
                    }
                }

                fadeIn.play();
            });

            fadeOut.play();
        }

        // Afficher un résumé des différences
        showComparisonSummary(data);
    }

    /**
     * Affiche un résumé des différences entre les deux périodes
     * @param data Les données comparatives
     */
    private void showComparisonSummary(Map<String, Object> data) {
        String nomPeriode1 = (String) data.get("nomPeriode1");
        String nomPeriode2 = (String) data.get("nomPeriode2");

        @SuppressWarnings("unchecked")
        Map<String, Object> donneesPeriode1 = (Map<String, Object>) data.get("periode1");

        @SuppressWarnings("unchecked")
        Map<String, Object> donneesPeriode2 = (Map<String, Object>) data.get("periode2");

        if (donneesPeriode1 == null || donneesPeriode2 == null) {
            return;
        }

        // Calculer les différences
        double diffOccupation = ((Number) donneesPeriode1.get("occupationRate")).doubleValue() -
                               ((Number) donneesPeriode2.get("occupationRate")).doubleValue();

        double diffPonctualite = ((Number) donneesPeriode1.get("punctualityRate")).doubleValue() -
                                ((Number) donneesPeriode2.get("punctualityRate")).doubleValue();

        double diffConsommation = ((Number) donneesPeriode1.get("consumptionRate")).doubleValue() -
                                 ((Number) donneesPeriode2.get("consumptionRate")).doubleValue();

        double diffSatisfaction = ((Number) donneesPeriode1.get("satisfactionRate")).doubleValue() -
                                 ((Number) donneesPeriode2.get("satisfactionRate")).doubleValue();

        // Afficher le résumé
        String message = String.format(
            "Comparaison entre %s et %s:\n\n" +
            "Taux d'occupation: %s%.1f%%\n" +
            "Ponctualité: %s%.1f%%\n" +
            "Consommation: %s%.1f L/100km\n" +
            "Satisfaction: %s%.1f points",
            nomPeriode1, nomPeriode2,
            diffOccupation >= 0 ? "+" : "", diffOccupation,
            diffPonctualite >= 0 ? "+" : "", diffPonctualite,
            diffConsommation >= 0 ? "+" : "", diffConsommation,
            diffSatisfaction >= 0 ? "+" : "", diffSatisfaction
        );

        showNotification("Résumé comparatif", message);
    }

    @FXML
    private void exportData(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Excel", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            Task<Void> exportTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // Logique d'exportation selon le type de fichier
                    if (file.getName().endsWith(".pdf")) {
                        exportToPDF(file);
                    } else if (file.getName().endsWith(".xlsx")) {
                        exportToExcel(file);
                    }
                    return null;
                }
            };

            exportTask.setOnSucceeded(e -> {
                showNotification("Exportation réussie", "Les données ont été exportées avec succès.");
            });

            new Thread(exportTask).start();
        }
    }

    @FXML
    private void toggleComparisonMode(ActionEvent event) {
        boolean isComparisonMode = ((ToggleButton) event.getSource()).isSelected();

        if (isComparisonMode) {
            // Afficher les contrôles de sélection de période de comparaison
            comparisonControls.setVisible(true);

            // Charger les données comparatives
            Task<Map<String, Object>> task = new Task<>() {
                @Override
                protected Map<String, Object> call() {
                    return StatistiquesAPI.getComparativeData(
                        periodeComboBox.getValue(),
                        comparisonPeriodeComboBox.getValue(),
                        typeBusComboBox.getValue(),
                        itineraireComboBox.getValue()
                    );
                }
            };

            task.setOnSucceeded(e -> {
                updateComparativeCharts(task.getValue());
            });

            new Thread(task).start();
        } else {
            // Cacher les contrôles de comparaison
            comparisonControls.setVisible(false);

            // Revenir à l'affichage normal
            loadData();
        }
    }

    private void adaptInterfaceToData(Map<String, Object> data) {
        // Adapter l'interface selon les données
        if (data.containsKey("occupationRate")) {
            double occupationRate = ((Number) data.get("occupationRate")).doubleValue();

            // Si occupation très élevée, mettre en évidence
            if (occupationRate > 85.0) {
                tauxOccupationLabel.getStyleClass().add("kpi-highlight");

                // Afficher des recommandations
                recommendationsContainer.getChildren().clear();
                Label recommendation = new Label("Occupation élevée : Envisager d'augmenter la fréquence des bus");
                recommendation.getStyleClass().add("recommendation-text");
                recommendationsContainer.getChildren().add(recommendation);
                recommendationsContainer.setVisible(true);
            } else {
                tauxOccupationLabel.getStyleClass().removeAll("kpi-highlight");
                recommendationsContainer.setVisible(false);
            }
        }
    }

    /**
     * Exporte les données statistiques au format PDF
     * @param file Le fichier de destination
     * @throws Exception Si une erreur survient pendant l'exportation
     */
    private void exportToPDF(File file) throws Exception {
        // Simulation d'un délai d'exportation
        Thread.sleep(1500);

        // Ici, vous devriez implémenter la logique d'exportation PDF
        // Par exemple, en utilisant une bibliothèque comme iText ou PDFBox

        System.out.println("Exportation en PDF vers : " + file.getAbsolutePath());

        // Pour une implémentation réelle, vous pourriez utiliser :
        // Document document = new Document();
        // PdfWriter.getInstance(document, new FileOutputStream(file));
        // document.open();
        // ... ajouter le contenu ...
        // document.close();
    }

    /**
     * Exporte les données statistiques au format Excel
     * @param file Le fichier de destination
     * @throws Exception Si une erreur survient pendant l'exportation
     */
    private void exportToExcel(File file) throws Exception {
        // Simulation d'un délai d'exportation
        Thread.sleep(1500);

        // Ici, vous devriez implémenter la logique d'exportation Excel
        // Par exemple, en utilisant une bibliothèque comme Apache POI

        System.out.println("Exportation en Excel vers : " + file.getAbsolutePath());

        // Pour une implémentation réelle, vous pourriez utiliser :
        // Workbook workbook = new XSSFWorkbook();
        // Sheet sheet = workbook.createSheet("Statistiques");
        // ... ajouter les données ...
        // FileOutputStream fileOut = new FileOutputStream(file);
        // workbook.write(fileOut);
        // fileOut.close();
        // workbook.close();
    }

    @FXML
    public void exportPDF(ActionEvent event) {
        try {
            // Generate a unique filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "statistiques_" + timestamp + ".pdf";
            String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

            // Create a new PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream for adding content to the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Rapport de Statistiques");
            contentStream.endText();

            // Add date
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            contentStream.newLineAtOffset(400, 680);
            contentStream.showText("Généré le: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            contentStream.endText();

            // Add statistics data
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(100, 650);
            contentStream.showText("Statistiques de Transport");
            contentStream.endText();

            // Add statistics details
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 620);
            contentStream.showText("Nombre total de bus: XX");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 600);
            contentStream.showText("Nombre total d'itinéraires: XX");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 580);
            contentStream.showText("Distance moyenne des itinéraires: XX km");
            contentStream.endText();

            // Close the content stream
            contentStream.close();

            // Save the document
            document.save(filePath);
            document.close();

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export réussi");
            alert.setHeaderText(null);
            alert.setContentText("Le rapport a été exporté avec succès dans: " + filePath);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'exportation: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode pour changer le type de graphique
    private void changeChartType(String type) {
        boolean useAnimation = true;
        String style = "-fx-stroke-width: 2px;";

        if ("line".equals(type)) {
            // Appliquer le style pour LineChart
            if (comparativeChart instanceof LineChart) {
                ((LineChart<?, ?>) comparativeChart).setCreateSymbols(true);
            }
        } else if ("bar".equals(type)) {
            // Appliquer le style pour BarChart
            // occupationChart est déjà un BarChart, pas besoin de conversion
            System.out.println("Application du style pour BarChart");
        }

        // Appliquer les animations
        if (occupationChart != null) {
            occupationChart.setAnimated(useAnimation);
        }

        if (comparativeChart != null) {
            comparativeChart.setAnimated(useAnimation);
        }
    }

    // Méthode pour créer un graphique programmatiquement si l'injection FXML échoue
    private void createOccupationChartIfNull() {
        if (occupationChart == null) {
            System.out.println("Création programmatique du graphique d'occupation...");

            // Créer les axes
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Période");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Taux (%)");

            // Créer le graphique
            occupationChart = new BarChart<>(xAxis, yAxis);
            occupationChart.setTitle("Taux d'occupation des bus");
            occupationChart.setPrefHeight(300);
            occupationChart.setPrefWidth(400);
            occupationChart.setAnimated(true);

            // Ajouter le graphique au conteneur principal
            mainContainer.getChildren().add(occupationChart);

            System.out.println("Graphique d'occupation créé avec succès");
        }
    }

    private void updateEvolutionChart(Map<String, Object> data) {
        try {
            // Récupérer les données mensuelles
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> monthlyData = (List<Map<String, Object>>) data.get("monthlyData");

            if (monthlyData == null || monthlyData.isEmpty()) {
                System.out.println("Aucune donnée mensuelle disponible");
                return;
            }

            // Créer une série pour le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre de passagers");

            // Ajouter les données à la série
            for (Map<String, Object> month : monthlyData) {
                String monthName = (String) month.get("month");
                Number passengers = (Number) month.get("passengers");
                series.getData().add(new XYChart.Data<>(monthName, passengers));
            }

            // Mettre à jour le graphique
            evolutionChart.getData().clear();
            evolutionChart.getData().add(series);

            System.out.println("Graphique d'évolution mis à jour avec " + series.getData().size() + " points");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du graphique d'évolution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateOccupationChart(Map<String, Object> data) {
        try {
            // Récupérer les données mensuelles
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> monthlyData = (List<Map<String, Object>>) data.get("monthlyData");

            if (monthlyData == null || monthlyData.isEmpty()) {
                System.out.println("Aucune donnée mensuelle disponible");
                return;
            }

            // Créer une série pour le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Taux d'occupation");

            // Ajouter les données à la série
            for (Map<String, Object> month : monthlyData) {
                String monthName = (String) month.get("month");
                Number occupationRate = (Number) month.get("occupationRate");
                series.getData().add(new XYChart.Data<>(monthName, occupationRate));
            }

            // Mettre à jour le graphique
            occupationChart.getData().clear();
            occupationChart.getData().add(series);

            System.out.println("Graphique d'occupation mis à jour avec " + series.getData().size() + " points");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du graphique d'occupation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTypeBusChart(Map<String, Object> data) {
        try {
            // Récupérer les données de types de bus
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> busTypeData = (List<Map<String, Object>>) data.get("busTypeData");

            if (busTypeData == null || busTypeData.isEmpty()) {
                System.out.println("Aucune donnée de types de bus disponible");
                return;
            }

            // Créer les données pour le graphique circulaire
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Ajouter les données au graphique
            for (Map<String, Object> type : busTypeData) {
                String typeName = (String) type.get("type");
                Number value = (Number) type.get("value");
                pieChartData.add(new PieChart.Data(typeName, value.doubleValue()));
            }

            // Mettre à jour le graphique
            typeBusChart.setData(pieChartData);

            System.out.println("Graphique de types de bus mis à jour avec " + pieChartData.size() + " segments");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du graphique de types de bus: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateItineraireChart(Map<String, Object> data) {
        try {
            // Récupérer les données d'itinéraires
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itineraireData = (List<Map<String, Object>>) data.get("itineraireData");

            if (itineraireData == null || itineraireData.isEmpty()) {
                System.out.println("Aucune donnée d'itinéraires disponible");
                return;
            }

            // Créer les données pour le graphique circulaire
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Ajouter les données au graphique
            for (Map<String, Object> route : itineraireData) {
                String routeName = (String) route.get("route");
                Number value = (Number) route.get("value");
                pieChartData.add(new PieChart.Data(routeName, value.doubleValue()));
            }

            // Mettre à jour le graphique
            itineraireChart.setData(pieChartData);

            System.out.println("Graphique d'itinéraires mis à jour avec " + pieChartData.size() + " segments");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du graphique d'itinéraires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateKPIs(Map<String, Object> data) {
        try {
            // Vérifier si les données sont présentes
            if (data == null) {
                System.err.println("Aucune donnée disponible pour mettre à jour les KPIs");
                return;
            }

            // Récupérer les valeurs des KPIs avec vérification de null
            double occupationRate = data.containsKey("occupationRate") ?
                ((Number) data.get("occupationRate")).doubleValue() : 0.0;
            double occupationEvolutionValue = data.containsKey("occupationEvolution") ?
                ((Number) data.get("occupationEvolution")).doubleValue() : 0.0;
            double punctualityRate = data.containsKey("punctualityRate") ?
                ((Number) data.get("punctualityRate")).doubleValue() : 0.0;
            double punctualityEvolutionValue = data.containsKey("punctualityEvolution") ?
                ((Number) data.get("punctualityEvolution")).doubleValue() : 0.0;
            double consumptionRate = data.containsKey("consumptionRate") ?
                ((Number) data.get("consumptionRate")).doubleValue() : 0.0;
            double consumptionEvolutionValue = data.containsKey("consumptionEvolution") ?
                ((Number) data.get("consumptionEvolution")).doubleValue() : 0.0;
            double satisfactionRate = data.containsKey("satisfactionRate") ?
                ((Number) data.get("satisfactionRate")).doubleValue() : 0.0;
            double satisfactionEvolutionValue = data.containsKey("satisfactionEvolution") ?
                ((Number) data.get("satisfactionEvolution")).doubleValue() : 0.0;

            // Mettre à jour les labels et les barres de progression
            if (tauxOccupationLabel != null) {
                tauxOccupationLabel.setText(String.format("%.1f%%", occupationRate));
                if (tauxOccupationProgress != null) {
                    tauxOccupationProgress.setProgress(occupationRate / 100.0);
                }
                if (tauxOccupationEvolution != null) {
                    String sign = occupationEvolutionValue >= 0 ? "+" : "";
                    tauxOccupationEvolution.setText(sign + String.format("%.1f%%", occupationEvolutionValue) + " vs période précédente");
                }
            }

            if (ponctualiteLabel != null) {
                ponctualiteLabel.setText(String.format("%.1f%%", punctualityRate));
                if (ponctualiteProgress != null) {
                    ponctualiteProgress.setProgress(punctualityRate / 100.0);
                }
                if (ponctualiteEvolution != null) {
                    String sign = punctualityEvolutionValue >= 0 ? "+" : "";
                    ponctualiteEvolution.setText(sign + String.format("%.1f%%", punctualityEvolutionValue) + " vs période précédente");
                }
            }

            if (consommationLabel != null) {
                consommationLabel.setText(String.format("%.1f L/100km", consumptionRate));
                if (consommationProgress != null) {
                    // Inverser la progression pour la consommation (moins c'est mieux)
                    consommationProgress.setProgress(1.0 - (consumptionRate / 40.0)); // 40 L/100km comme max
                }
                if (consommationEvolution != null) {
                    // Pour la consommation, une évolution négative est positive
                    String sign = consumptionEvolutionValue <= 0 ? "+" : "";
                    consommationEvolution.setText(sign + String.format("%.1f%%", Math.abs(consumptionEvolutionValue)) + " vs période précédente");
                }
            }

            // Mettre à jour la satisfaction (étoiles)
            if (satisfactionLabel != null) {
                satisfactionLabel.setText(String.format("%.1f/5", satisfactionRate));

                // Mettre à jour les étoiles si le conteneur existe
                if (starsContainer != null) {
                    starsContainer.getChildren().clear();

                    // Créer les étoiles en fonction de la note
                    for (int i = 1; i <= 5; i++) {
                        SVGPath star = new SVGPath();

                        // Utiliser une étoile pleine, à moitié pleine ou vide selon la note
                        if (i <= Math.floor(satisfactionRate)) {
                            // Étoile pleine
                            star.setContent("M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z");
                            star.setFill(Color.GOLD);
                        } else if (i <= Math.ceil(satisfactionRate) && i > Math.floor(satisfactionRate)) {
                            // Étoile à moitié pleine
                            star.setContent("M12,15.4V6.1L13.71,10.13L18.09,10.5L14.77,13.39L15.76,17.67L12,15.4M22,9.24L14.81,8.63L12,2L9.19,8.63L2,9.24L7.45,13.97L5.82,21L12,17.27L18.18,21L16.54,13.97L22,9.24Z");
                            star.setFill(Color.GOLD);
                        } else {
                            // Étoile vide
                            star.setContent("M12,15.39L8.24,17.66L9.23,13.38L5.91,10.5L10.29,10.13L12,6.09L13.71,10.13L18.09,10.5L14.77,13.38L15.76,17.66M22,9.24L14.81,8.63L12,2L9.19,8.63L2,9.24L7.45,13.97L5.82,21L12,17.27L18.18,21L16.54,13.97L22,9.24Z");
                            star.setFill(Color.GRAY);
                        }

                        star.setScaleX(1.5);
                        star.setScaleY(1.5);
                        starsContainer.getChildren().add(star);
                    }
                }

                if (satisfactionEvolution != null) {
                    String sign = satisfactionEvolutionValue >= 0 ? "+" : "";
                    satisfactionEvolution.setText(sign + String.format("%.1f", satisfactionEvolutionValue) + " vs période précédente");
                }
            }

            System.out.println("KPIs mis à jour avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des KPIs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
