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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifierRevenuController implements Initializable {

    private final RevenuService revenuService = new RevenuService();
    private Revenu currentRevenu;

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
    private Button btnModifier;

    @FXML
    private Button btnRetour;
    @FXML
    private TextArea TFFactureDetails;
    private MenuRevenuController menuRevenuController;

    public void setMenuRevenuController(MenuRevenuController controller) {
        this.menuRevenuController = controller;
    }

    public void setRevenuData(Revenu revenu) {
        this.currentRevenu = revenu;
        remplirChamps(revenu);
    }

    private void remplirChamps(Revenu revenu) {
        TFMontant.setText(String.valueOf(revenu.getMontant()));
        TFDate.setValue(revenu.getDate());
        TFTypeRevenu.setValue(revenu.getTypeRevenu());
        TFStatut.setValue(revenu.getStatut());
        TFSource.setText(revenu.getSource());
        TFClient.setText(revenu.getClient());
        TFFactureDetails.setText(revenu.getFactureDetails());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> typesRevenu = FXCollections.observableArrayList(
                "ticket simple", "réservation en ligne", "publicité", "pénalité de retard", "Autres"
        );
        TFTypeRevenu.setItems(typesRevenu);

        ObservableList<String> statuts = FXCollections.observableArrayList(
                "Reçu", "En attente", "Annulé"
        );
        TFStatut.setItems(statuts);
    }
    @FXML
    void Retour(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menuRevenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    void Modifier(ActionEvent event) {
        try {
            if (currentRevenu == null) {
                showError("Aucun revenu à modifier.");
                return;
            }

            if (TFMontant.getText().isEmpty() ||
                    TFDate.getValue() == null ||
                    TFTypeRevenu.getValue() == null ||
                    TFStatut.getValue() == null ||
                    TFSource.getText().isEmpty() ||
                    TFClient.getText().isEmpty()) {
                showError("Veuillez remplir tous les champs !");
                return;
            }

            double montant;
            try {
                montant = Double.parseDouble(TFMontant.getText());
            } catch (NumberFormatException e) {
                showError("Montant invalide !");
                return;
            }

            currentRevenu.setMontant(montant);
            currentRevenu.setDate(TFDate.getValue());
            currentRevenu.setTypeRevenu(TFTypeRevenu.getValue());
            currentRevenu.setStatut(TFStatut.getValue());
            currentRevenu.setSource(TFSource.getText());
            currentRevenu.setClient(TFClient.getText());
            currentRevenu.setFactureDetails(TFFactureDetails.getText());

            revenuService.update(currentRevenu);

            showInfo("Revenu modifié avec succès.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/menuRevenu.fxml"));
            Parent root = loader.load();

            MenuRevenuController menuController = loader.getController();
            menuController.chargerDonnees();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la mise à jour du revenu.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
