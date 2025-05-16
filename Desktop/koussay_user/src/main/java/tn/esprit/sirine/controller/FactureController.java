package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import tn.esprit.sirine.models.Revenu;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import tn.esprit.sirine.utils.PdfGenerator;


public class FactureController {
    @FXML private Label numeroFacture;
    @FXML private Label dateFacture;
    @FXML private Label clientNom;
    @FXML private Label clientDetails;
    @FXML private TableView<Revenu> detailsTable;
    @FXML private Label sousTotal;
    @FXML private Label tva;
    @FXML private Label total;
    @FXML
    private Button btnExporter;

    @FXML
    private Button btnRetour;
    @FXML private TableColumn<Revenu, Double> colMontant;
    @FXML private TableColumn<Revenu, String> colSource;
    @FXML private TableColumn<Revenu, String> colType;

    @FXML
    public void initialize() {

        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRevenu"));
    }

    private Revenu revenu;

    // Méthode pour définir l'objet Revenu
    public void setRevenu(Revenu revenu) {
        this.revenu = revenu;
        remplirDonnees();
    }

    private void remplirDonnees() {
        // Remplir les informations de base
        numeroFacture.setText("FACT-" + revenu.getIdRevenu());
        dateFacture.setText(revenu.getDate().toString());
        clientNom.setText(revenu.getClient());
        clientDetails.setText(revenu.getFactureDetails());

        // Calculs des totaux
        double montantHT = revenu.getMontant();
        double montantTVA = montantHT * 0.19;
        double montantTTC = montantHT + montantTVA;

        sousTotal.setText(String.format("%.2f TND", montantHT));
        tva.setText(String.format("%.2f TND", montantTVA));
        total.setText(String.format("%.2f TND", montantTTC));

        // Ajouter le revenu au tableau
        detailsTable.getItems().add(revenu);
    }

    @FXML
    private void retour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/menuRevenu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void exporterPDF(ActionEvent event) {
        if (revenu == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Aucun revenu à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("Facture_" + revenu.getClient() + ".pdf");

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                // Nouvelle méthode utilisant PDFBox
                PdfGenerator.generateRevenuPdf(revenu, file);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture enregistrée avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement du PDF : " + e.getMessage());
            }
        }
    }


    @FXML
    private Button btnExportExcel;



    @FXML
    private void exporterExcel(ActionEvent event) {
        if (revenu == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Aucun revenu à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("Facture_" + revenu.getClient() + ".xlsx");

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Facture");

                // En-têtes
                String[] headers = {"Client", "Date", "Montant", "Source", "Détails"};
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                // Valeurs
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(1);
                row.createCell(0).setCellValue(revenu.getClient());
                row.createCell(1).setCellValue(revenu.getDate().toString());
                row.createCell(2).setCellValue(revenu.getMontant());
                row.createCell(3).setCellValue(revenu.getSource());
                row.createCell(4).setCellValue(revenu.getFactureDetails());

                // Auto-sizing
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture exportée en Excel !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'export : " + e.getMessage());
            }
        }
    }


}
