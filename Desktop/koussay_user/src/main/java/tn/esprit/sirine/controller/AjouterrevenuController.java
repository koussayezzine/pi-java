package tn.esprit.sirine.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Revenu;
import tn.esprit.sirine.services.RevenuService;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;  // Importation de LocalDate
import java.util.ResourceBundle;

public class AjouterrevenuController implements Initializable {

    private final RevenuService revenuService = new RevenuService();



    @FXML
    private TextField TFMontant;

    @FXML
    private DatePicker TFDate;

    @FXML
    private ComboBox<String> TFTypeRevenu;

    @FXML
    private ComboBox<String> TFStatut;

    @FXML
    private TextField TFSource;

    @FXML
    private TextField TFClient;

    @FXML
    private TextArea TFFactureDetails;
    @FXML
    private Button btnRetour;

    private MenuRevenuController MenuRevenuController;

    public void setMenuRevenuController(MenuRevenuController controller) {
        this.MenuRevenuController = controller;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les types de revenu
        ObservableList<String> typesRevenu = FXCollections.observableArrayList(
                "ticket simple", "réservation en ligne", "publicité", "pénalité de retard", "Autres"
        );
        TFTypeRevenu.setItems(typesRevenu);

        // Initialiser les statuts
        ObservableList<String> statuts = FXCollections.observableArrayList(
                "Reçu", "En attente", "Annulé"
        );
        TFStatut.setItems(statuts);

        // Par défaut, mettre la date d'aujourd'hui
        TFDate.setValue(LocalDate.now());
    }
    @FXML
    void retour(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menuRevenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }



    @FXML
    void ajouter(ActionEvent event) {
        try {
            // Vérifier les champs obligatoires
            if (TFMontant.getText().isEmpty() ||
                    TFDate.getValue() == null ||
                    TFTypeRevenu.getValue() == null ||
                    TFStatut.getValue() == null ||
                    TFSource.getText().isEmpty() ||
                    TFClient.getText().isEmpty()) {
                showError("Veuillez remplir tous les champs requis !");
                return;
            }

            // Valider le montant
            double montant;
            try {
                montant = Double.parseDouble(TFMontant.getText());
            } catch (NumberFormatException e) {
                showError("Montant invalide. Veuillez entrer un nombre valide.");
                return;
            }

            // Utiliser LocalDate directement sans conversion en java.sql.Date
            LocalDate localDate = TFDate.getValue();  // LocalDate de Java 8+

            // Lire les autres champs
            String source = TFSource.getText();
            String typeRevenu = TFTypeRevenu.getValue();
            String statut = TFStatut.getValue();
            String client = TFClient.getText();
            String factureDetails = TFFactureDetails.getText();

            // Créer l'objet Revenu sans idRevenu, la base de données générera cet ID
            Revenu revenu = new Revenu(
                    montant, localDate, source, typeRevenu, statut, client, factureDetails
            );

            // Appeler le service pour insérer le revenu dans la base de données
            revenuService.create(revenu);

            // Succès ✅
            showInfo("Revenu ajouté avec succès !");

            // Réinitialiser les champs après ajout
            resetFields();

        } catch (SQLException e) {
            showError("Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            showError("Erreur inattendue : " + e.getMessage());
        }
    }




    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetFields() {

        TFMontant.clear();
        TFDate.setValue(LocalDate.now());
        TFTypeRevenu.setValue(null);
        TFStatut.setValue(null);
        TFSource.clear();
        TFClient.clear();
        TFFactureDetails.clear();
    }
}
