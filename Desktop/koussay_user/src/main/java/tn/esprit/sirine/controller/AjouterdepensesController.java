package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;


import tn.esprit.sirine.models.Depenses;
import tn.esprit.sirine.services.DepensesService;

public class AjouterdepensesController {

    @FXML private ComboBox<String> TFCategorie;
    @FXML private DatePicker TFDate;
    @FXML private TextArea TFDescription;
    @FXML private TextField TFFournisseur;
    @FXML private TextField TFJustificatif;
    @FXML private TextField TFMontant;
    @FXML private ComboBox<String> TFStatut;
    @FXML
    private Button buttonjust;

    private DepensesService depensesService;
    menuController menuController; // Référence vers le contrôleur menu

    // Méthode pour recevoir le contrôleur du menu
    public void setmenuController(menuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    public void initialize() {
        TFCategorie.getItems().addAll("maintenance", "carburant", "matériel informatique", "hébergement web", "achat de licences", "marketing", "Autres");
        TFStatut.getItems().addAll("En attente", "Payé", "Annulé");

        try {
            depensesService = new DepensesService();
            System.out.println("Service de dépenses initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation du service : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void Retour(ActionEvent event) throws Exception {
        // Retour au menu (sans transmettre la référence)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Ajouter() {
        try {
            // Validation des champs
            if (TFMontant.getText().isEmpty() || TFDate.getValue() == null ||
                    TFCategorie.getValue() == null || TFStatut.getValue() == null ||
                    TFFournisseur.getText().isEmpty() || TFDescription.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Veuillez remplir tous les champs obligatoires.");
                alert.showAndWait();
                return;
            }

            // Vérification du format du montant
            double montant;
            try {
                montant = Double.parseDouble(TFMontant.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Le montant doit être un nombre valide.");
                alert.showAndWait();
                return;
            }

            // Création de la dépense
            Depenses depense = new Depenses();
            depense.setMontant(montant);
            depense.setDate(java.sql.Date.valueOf(TFDate.getValue()));
            depense.setCategorie(TFCategorie.getValue());
            depense.setStatut(TFStatut.getValue());
            depense.setFournisseur(TFFournisseur.getText());
            depense.setDescription(TFDescription.getText());
            depense.setJustificatif(TFJustificatif.getText().isEmpty() ? null : TFJustificatif.getText());

            System.out.println("Tentative d'ajout de la dépense : " + depense.getMontant() + " - " + depense.getCategorie());

            // Insertion dans la base
            depensesService.create(depense);
            System.out.println("Dépense ajoutée avec succès dans la base de données");

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Dépense ajoutée avec succès !");
            alert.showAndWait();

            // Retour au menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menu.fxml"));
            Parent root = loader.load();
            menuController menuController = loader.getController();
            if (menuController != null) {
                menuController.chargerDonnees();
            }

            Stage stage = (Stage) TFMontant.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'ajout de la dépense : " + ex.getMessage());
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur est survenue lors de l'ajout : " + ex.getMessage());
            alert.showAndWait();
        }
    }
    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
