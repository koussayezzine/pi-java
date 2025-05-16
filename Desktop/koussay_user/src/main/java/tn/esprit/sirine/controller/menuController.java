package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import tn.esprit.sirine.models.Depenses;
import tn.esprit.sirine.services.DepensesService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;


import java.util.Optional;





import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class menuController implements Initializable {
    @FXML
    private TableView<Depenses> tableViewDepenses;
    @FXML
    private TableColumn<Depenses, Double> colMontant;
    @FXML
    private TableColumn<Depenses, Date> colDate;
    @FXML
    private TableColumn<Depenses, String> colDescription;
    @FXML
    private TableColumn<Depenses, String> colCategorie;
    @FXML
    private TableColumn<Depenses, String> colJustificatif;
    @FXML
    private TableColumn<Depenses, String> colStatut;
    @FXML
    private TableColumn<Depenses, String> colFournisseur;
    @FXML
    private Button labelAjouter;
    @FXML
    private Button labelModifier;
    @FXML
    private Button labelRetour;
    @FXML
    private Button labelSupprimer;
    @FXML
    private Button labelchercher;


    private DepensesService depensesService = new DepensesService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colJustificatif.setCellValueFactory(new PropertyValueFactory<>("justificatif"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));

        chargerDonnees();

        tableViewDepenses.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Dépense sélectionnée: " + newValue.getDescription());
            }
        });

        // 🔥 Ajoute cette ligne pour déclencher la recherche automatiquement
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            chercher(null);
        });
    }


    public void chargerDonnees() {
        try {
            List<Depenses> depensesList = depensesService.readAll();
            ObservableList<Depenses> observableList = FXCollections.observableArrayList();

            for (Depenses d : depensesList) {
                Depenses copie = new Depenses();
                copie.setIdDepense(d.getIdDepense()); // ⚠️ C’est ici qu'on corrige
                copie.setMontant(d.getMontant());
                copie.setDate(d.getDate());
                copie.setCategorie(d.getCategorie());
                copie.setDescription(d.getDescription());
                copie.setJustificatif(d.getJustificatif());
                copie.setStatut(d.getStatut());
                copie.setFournisseur(d.getFournisseur());

                observableList.add(copie);
            }

            tableViewDepenses.setItems(observableList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateDepense(Depenses modifiedDepense) {
        // Trouver la dépense dans la liste observable
        for (Depenses depense : tableViewDepenses.getItems()) {
            {
                // Mettre à jour la dépense correspondante
                depense.setMontant(modifiedDepense.getMontant());
                depense.setDate(modifiedDepense.getDate());
                depense.setCategorie(modifiedDepense.getCategorie());
                depense.setDescription(modifiedDepense.getDescription());
                depense.setStatut(modifiedDepense.getStatut());
                depense.setFournisseur(modifiedDepense.getFournisseur());
                break;
            }
        }
        // Rafraîchir la vue
        tableViewDepenses.refresh();
    }


    @FXML
    private void Ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/SCENE.fxml"));
            Parent root = loader.load();

            AjouterdepensesController ajoutController = loader.getController();
            ajoutController.setmenuController(this); // passer la référence

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(menuController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @FXML
    void Modifier(ActionEvent event) throws IOException {
        Depenses selected = tableViewDepenses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une dépense à modifier.");
            alert.showAndWait();
            return;
        }

        // Créer une nouvelle instance de Depenses pour éviter les références partagées
        Depenses depenseToModify = new Depenses();
        depenseToModify.setIdDepense(selected.getIdDepense());
        depenseToModify.setMontant(selected.getMontant());
        depenseToModify.setDate(selected.getDate());
        depenseToModify.setCategorie(selected.getCategorie());
        depenseToModify.setDescription(selected.getDescription());
        depenseToModify.setStatut(selected.getStatut());
        depenseToModify.setFournisseur(selected.getFournisseur());
        depenseToModify.setJustificatif(selected.getJustificatif());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/modifier.fxml"));
        Parent root = loader.load();

        ModifierdepensesController modifierController = loader.getController();
        modifierController.setDepenseData(depenseToModify);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Retour(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/sirine/Dashboard.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) labelRetour.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void Supprimer(ActionEvent event) {
        Depenses selected = tableViewDepenses.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une dépense à supprimer.");
            alert.showAndWait();
            return;
        }

        // Boîte de confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette dépense ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                try {
                    // Supprimer de la base de données
                    depensesService.delete(selected.getIdDepense());

                    // Supprimer de la table
                    tableViewDepenses.getItems().remove(selected);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Suppression réussie");
                    alert.setContentText("La dépense a été supprimée.");
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setContentText("Erreur lors de la suppression de la dépense.");
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    private javafx.scene.control.TextField searchField;

    @FXML
    void chercher(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            chargerDonnees();  // Recharge toutes les données si la recherche est vide
            return;
        }

        try {
            List<Depenses> depensesList = depensesService.readAll();
            List<Depenses> filteredList = depensesList.stream()
                    .filter(dep -> dep.getStatut() != null && dep.getStatut().toLowerCase().contains(keyword))
                    .toList();

            ObservableList<Depenses> observableList = FXCollections.observableArrayList(filteredList);
            tableViewDepenses.setItems(observableList);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du filtrage des dépenses.");
            alert.showAndWait();
        }
    }

    @FXML
    private Button labelExporterCSV;

    @FXML
    private void exporterCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("Montant,Date,Description,Catégorie,Justificatif,Statut,Fournisseur");

                for (Depenses d : tableViewDepenses.getItems()) {
                    String line = String.format(Locale.US, "%.2f,%s,%s,%s,%s,%s,%s",
                            d.getMontant(),
                            d.getDate(),
                            escapeCSV(d.getDescription()),
                            escapeCSV(d.getCategorie()),
                            escapeCSV(d.getJustificatif()),
                            escapeCSV(d.getStatut()),
                            escapeCSV(d.getFournisseur())
                    );
                    writer.println(line);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setContentText("Les données ont été exportées vers le fichier CSV.");
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText("Erreur lors de l'exportation du fichier CSV.");
                alert.showAndWait();
            }
        }
    }

    // Échappe les virgules et guillemets pour le CSV
    private String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void genererFacturePdf(ActionEvent event) {
        Depenses selected = tableViewDepenses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une dépense pour générer la facture.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("facture_depense.pdf");

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                tn.esprit.sirine.utils.PdfGeneratorDepense.generateDepensePdf(selected, file);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture générée avec succès !");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
            }
        }
    }
}