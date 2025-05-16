package tn.esprit.sirine.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Revenu;
import javafx.scene.control.Button;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ComboBox<String> comboCritere;
    @FXML
    private Button btnRetour;

    private List<Revenu> revenus;

    // Méthode d'initialisation
    public void initialize() {
        comboCritere.getItems().addAll("Par Client", "Par Source", "Par Montant");
        comboCritere.setOnAction(e -> updateChart());
        comboCritere.getSelectionModel().selectFirst();
    }

    // Méthode pour définir les revenus
    public void setRevenu(List<Revenu> revenus) {
        this.revenus = revenus;
        updateChart();  // Mettre à jour le graphique dès que les revenus sont définis
    }

    // Méthode pour obtenir la plage de montants
    private String getMontantRange(double montant) {
        if (montant < 100) {
            return "< 100";
        } else if (montant < 500) {
            return "100 - 499";
        } else if (montant < 1000) {
            return "500 - 999";
        } else {
            return ">= 1000";
        }
    }

    // Méthode pour calculer les statistiques supplémentaires
    private Map<String, Double> calculateStatistics(List<Revenu> revenus) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Revenu revenu : revenus) {
            stats.addValue(revenu.getMontant());
        }

        Map<String, Double> statistics = new HashMap<>();
        statistics.put("Moyenne", stats.getMean());
        statistics.put("Ecart-type", stats.getStandardDeviation());
        statistics.put("Min", stats.getMin());
        statistics.put("Max", stats.getMax());

        return statistics;
    }

    // Méthode pour mettre à jour le graphique
    private void updateChart() {
        if (revenus == null || revenus.isEmpty()) {
            return; // Ne rien faire si la liste est vide ou non initialisée
        }

        String selected = comboCritere.getSelectionModel().getSelectedItem();
        Map<String, Double> groupedData = new HashMap<>();

        // Regroupement des données en fonction de la sélection du ComboBox
        switch (selected) {
            case "Par Client":
                groupedData = revenus.stream().collect(
                        Collectors.groupingBy(Revenu::getClient,
                                Collectors.summingDouble(Revenu::getMontant)));
                break;
            case "Par Source":
                groupedData = revenus.stream().collect(
                        Collectors.groupingBy(Revenu::getSource,
                                Collectors.summingDouble(Revenu::getMontant)));
                break;
            case "Par Montant":
                groupedData = revenus.stream().collect(
                        Collectors.groupingBy(r -> getMontantRange(r.getMontant()),
                                Collectors.summingDouble(Revenu::getMontant)));
                break;
        }

        // Mise à jour du graphique
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Ajout des données au graphique
        groupedData.forEach((key, value) -> {
            series.getData().add(new XYChart.Data<>(key, value));
        });

        // Ajout de la série de données au graphique
        barChart.getData().add(series);

        // Calcul des statistiques supplémentaires
        Map<String, Double> statistics = calculateStatistics(revenus);

        // Afficher les statistiques dans la console (ou dans l'UI si nécessaire)
        System.out.println("Moyenne des Montants: " + statistics.get("Moyenne"));
        System.out.println("Ecart-type des Montants: " + statistics.get("Ecart-type"));
        System.out.println("Min: " + statistics.get("Min"));
        System.out.println("Max: " + statistics.get("Max"));
    }

    // Méthode pour revenir au menu précédent
    @FXML
    private void retour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/menuRevenu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
