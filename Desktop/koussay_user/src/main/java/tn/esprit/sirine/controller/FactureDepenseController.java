package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Depenses;
import tn.esprit.sirine.utils.PdfGeneratorDepense;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class FactureDepenseController {

    @FXML
    private Label numeroFacture;
    @FXML
    private Label dateFacture;
    @FXML
    private Label fournisseur;
    @FXML
    private Label statut;

    @FXML
    private TableView<Depenses> detailsTable;
    @FXML
    private TableColumn<Depenses, Double> colMontant;
    @FXML
    private TableColumn<Depenses, String> colCategorie;
    @FXML
    private TableColumn<Depenses, String> colDescription;

    @FXML
    private Label sousTotal;
    @FXML
    private Label tva;
    @FXML
    private Label total;

    private Depenses depense;

    public void setDepense(Depenses depense) {
        this.depense = depense;


        dateFacture.setText(depense.getDate().toString());
        fournisseur.setText(depense.getFournisseur());
        statut.setText(depense.getStatut());


        // Remplir la table avec un seul élément pour affichage
        ObservableList<Depenses> list = FXCollections.observableArrayList(depense);
        detailsTable.setItems(list);

        // Initialisation des colonnes
        colMontant.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getMontant()));
        colCategorie.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategorie()));
        colDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        // Calculs
        double montant = depense.getMontant();
        double montantTva = montant * 0.19;
        double montantTtc = montant + montantTva;

        sousTotal.setText(String.format("%.2f €", montant));
        tva.setText(String.format("%.2f €", montantTva));
        total.setText(String.format("%.2f €", montantTtc));
    }

    @FXML
    private void exporterPDF(ActionEvent event) {
        if (depense == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la facture PDF");
        fileChooser.setInitialFileName("facture_depense_" + depense.getFournisseur() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                PdfGeneratorDepense.generateDepensePdf(depense, file);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture PDF exportée avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'exportation PDF : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exporterExcel(ActionEvent event) {
        // TODO: implémenter l'export Excel si nécessaire
        showAlert(Alert.AlertType.INFORMATION, "Info", "Fonctionnalité d'export Excel non encore implémentée.");
    }

    @FXML
    private void retour(ActionEvent event) {
        // Ferme la fenêtre actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
