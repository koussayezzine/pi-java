package tn.esprit.sirine.controller;

// Ajoutez ces imports au début du fichier
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import tn.esprit.sirine.models.Depenses;
import tn.esprit.sirine.services.DepensesService;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.Node;


import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.event.ActionEvent;

public class ModifierdepensesController {

    @FXML
    private Button ModifierButton;

    @FXML
    private ComboBox<String> TFCategorie;

    @FXML
    private DatePicker TFDate;

    @FXML
    private TextArea TFDescription;

    @FXML
    private TextField TFFournisseur;

    @FXML
    private TextField TFJustificatif;

    @FXML
    private TextField TFMontant;

    @FXML
    private ComboBox<String> TFStatut;

    @FXML
    private Button labelRetour;

    private DepensesService depensesService;
    private Depenses currentDepense;

    // ✅ Constructor pour initialiser depensesService
    public ModifierdepensesController() {
        depensesService = new DepensesService();
    }
    @FXML
    public void initialize() {
        // Liste des catégories possibles
        TFCategorie.getItems().addAll(
                "maintenance", "carburant", "matériel informatique", "hébergement web", "achat de licences", "marketing", "Autres"
        );

        // Liste des statuts possibles
        TFStatut.getItems().addAll(
                "Payée", "En attente", "Annulée"
        );
    }

    // Le reste de tes méthodes FXML
    @FXML
    void Retour(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menu.fxml"));
        Parent root = loader.load();

        // Récupérer le contrôleur du menu et rafraîchir les données
        menuController controller = loader.getController();
        controller.chargerDonnees();

        Stage stage = (Stage) labelRetour.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setDepenseData(Depenses depense) {
        if (TFCategorie.getItems().isEmpty()) {
            TFCategorie.getItems().addAll("Loyer", "Électricité", "Eau", "Internet", "Transport", "Fournitures", "Salaires", "Autres");
        }
        if (TFStatut.getItems().isEmpty()) {
            TFStatut.getItems().addAll("Payée", "En attente", "Annulée");
        }

        currentDepense = depense;
        TFMontant.setText(String.valueOf(depense.getMontant()));
        TFDate.setValue(((java.sql.Date) depense.getDate()).toLocalDate());
        TFCategorie.setValue(depense.getCategorie());
        TFDescription.setText(depense.getDescription());
        TFStatut.setValue(depense.getStatut());
        TFFournisseur.setText(depense.getFournisseur());
        TFJustificatif.setText(depense.getJustificatif()); // ✅ <-- AJOUTER CECI
    }

    @FXML
    private void Modifier(ActionEvent event) {
        try {
            if (currentDepense == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Aucune dépense à modifier.");
                alert.showAndWait();
                return;
            }

            if (TFMontant.getText().isEmpty() || TFDate.getValue() == null ||
                    TFCategorie.getValue() == null || TFStatut.getValue() == null ||
                    TFDescription.getText().isEmpty() || TFFournisseur.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs manquants");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            }

            // Mise à jour des valeurs
            currentDepense.setMontant(Double.parseDouble(TFMontant.getText()));
            currentDepense.setDate(java.sql.Date.valueOf(TFDate.getValue()));
            currentDepense.setCategorie(TFCategorie.getValue());
            currentDepense.setDescription(TFDescription.getText());
            currentDepense.setStatut(TFStatut.getValue());
            currentDepense.setFournisseur(TFFournisseur.getText());
            currentDepense.setJustificatif(TFJustificatif.getText()); // si nécessaire

            // Mettre à jour la base de données
            depensesService.update(currentDepense);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification réussie");
            alert.setContentText("Dépense modifiée avec succès.");
            alert.showAndWait();

            // Retourner à la page menu après la modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menu.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur du menu et rafraîchir les données
            menuController controller = loader.getController();
            controller.chargerDonnees();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la mise à jour de la dépense.");
            alert.showAndWait();
        }
    }





    private void clearForm() {
        TFMontant.clear();
        TFDate.setValue(null);
        TFCategorie.setValue(null);
        TFDescription.clear();
        TFStatut.setValue(null);
        TFFournisseur.clear();
        TFJustificatif.clear();
        currentDepense = null;
    }


}
