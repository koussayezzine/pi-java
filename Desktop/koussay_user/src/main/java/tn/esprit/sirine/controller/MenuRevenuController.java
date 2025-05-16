package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import tn.esprit.sirine.models.Revenu;
import tn.esprit.sirine.services.RevenuService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MenuRevenuController implements Initializable {
    @FXML private TableView<Revenu> tableViewRevenu;
    @FXML private TableColumn<Revenu, Double> colMontant;
    @FXML private TableColumn<Revenu, LocalDate> colDate;
    @FXML private TableColumn<Revenu, String> colSource;
    @FXML private TableColumn<Revenu, String> colTypeRevenu;
    @FXML private TableColumn<Revenu, String> colStatut;
    @FXML private TableColumn<Revenu, String> colClient;
    @FXML private TableColumn<Revenu, String> colFactureDetails;

    @FXML private TextField searchField;
    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnChercher;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnStatistiques;
    @FXML
    private Button btnGenererPdf;
    public class PdfGenerator {
        public static void generateRevenuPdf(Revenu revenu) throws IOException {
            // Implémente avec iText (ou je peux te générer l'exemple complet si tu veux)
        }
    }
    @FXML
    void genererFacturePdf(ActionEvent event) {
        Revenu selected = tableViewRevenu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un revenu pour générer le PDF.");
            return;
        }

        try {
            // Charge la nouvelle page facture.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/facture.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur de la page facture
            FactureController factureController = loader.getController();

            // Passe l'objet 'Revenu' sélectionné au contrôleur facture
            factureController.setRevenu(selected);

            // Crée une nouvelle scène avec la vue facture
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Génère le PDF de la facture
            PdfGenerator.generateRevenuPdf(selected);

            // Affiche une alerte de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture générée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la génération du PDF : " + e.getMessage());
        }
    }

    @FXML
    void afficherStatistiques(ActionEvent event) {
        try {
            // Charger la vue des statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/statistiques.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur des statistiques
            StatistiquesController statistiquesController = loader.getController();

            // Passer la liste des revenus au contrôleur des statistiques
            statistiquesController.setRevenu(allRevenus);  // Ici on passe la liste de tous les revenus

            // Créer un nouveau stage pour afficher les statistiques
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'affichage des statistiques : " + e.getMessage());
        }
    }


    @FXML
    private Button btnRetour;

    @FXML
    private Button btnSupprimer;

    private RevenuService revenuService = new RevenuService();
    private List<Revenu> allRevenus;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colTypeRevenu.setCellValueFactory(new PropertyValueFactory<>("typeRevenu"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colFactureDetails.setCellValueFactory(new PropertyValueFactory<>("factureDetails"));

        chargerDonnees();
        searchField.textProperty().addListener((obs, oldText, newText) -> chercher(null));
    }

    public void chargerDonnees() {
        try {
            allRevenus = revenuService.readAll();
            tableViewRevenu.getItems().setAll(allRevenus);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    void Ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/Revenu.fxml"));
            Parent root = loader.load();

            AjouterrevenuController ajoutController = loader.getController();
            ajoutController.setMenuRevenuController(this);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
        }
    }

    @FXML
    void Modifier(ActionEvent event) throws IOException {
        Revenu selected = tableViewRevenu.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un revenu à modifier.");
            return;
        }

        // Chargement de la vue de modification
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/modifierRevenu.fxml"));
        Parent root = loader.load();

        // Création d'une nouvelle scène
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Important : Attendre que la scène soit affichée avant d'appeler setRevenuData
        stage.show();

        // Ensuite, configurer le contrôleur
        ModifierRevenuController modifierController = loader.getController();
        modifierController.setRevenuData(selected);
        modifierController.setMenuRevenuController(this);
    }



    @FXML
    void Supprimer(ActionEvent event) {
        Revenu selected = tableViewRevenu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un revenu à supprimer.");
            return;
        }

        try {
            revenuService.delete(selected.getIdRevenu());
            chargerDonnees();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Revenu supprimé avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }
    @FXML
    void chercher(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            tableViewRevenu.getItems().setAll(allRevenus);  // Restaurer les données complètes
            return;
        }

        List<Revenu> filtered = allRevenus.stream()
                .filter(r -> r.getClient().toLowerCase().contains(keyword)
                        || r.getSource().toLowerCase().contains(keyword)
                        || String.valueOf(r.getMontant()).contains(keyword))
                .collect(Collectors.toList());

        tableViewRevenu.getItems().setAll(filtered);
    }


    @FXML
    void Retour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/Dashboard.fxml"));
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
