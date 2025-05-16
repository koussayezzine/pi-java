package org.example.controllers;

import org.example.models.EtatTicket;
import org.example.models.Ticket;
import org.example.service.TicketService;
import org.example.util.DBconnection;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class SupprimerTicket {

    @FXML private BorderPane rootPane;
    @FXML private Label passengerLabel;
    @FXML private Label priceLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateLabel;
    @FXML private Button deleteButton;
    @FXML private Button exportButton;
    @FXML private Button btnRetour;
    @FXML private Label messageLabel;
    @FXML private ImageView qrCodeImage;

    private TicketService ticketService;
    private Ticket selectedTicket;

    public SupprimerTicket() {
        Connection conn = DBconnection.getInstance().getConn();
        if (conn == null) {
            System.err.println("Erreur : Connexion à la base de données nulle");
        }
        ticketService = new TicketService(conn);
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation de SupprimerTicket");
        if (ticketService == null) {
            showMessage("Impossible de se connecter à la base de données", false);
            deleteButton.setDisable(true);
            exportButton.setDisable(true);
            return;
        }
        // Désactiver les boutons par défaut
        deleteButton.setDisable(true);
        exportButton.setDisable(true);
        System.out.println("SupprimerTicket initialisé avec succès");
    }

    // Pré-remplir les détails du ticket sélectionné
    public void setSelectedTicket(Ticket ticket) {
        System.out.println("setSelectedTicket appelé avec ticket : " + (ticket != null ? ticket.toString() : "null"));
        if (ticket != null) {
            this.selectedTicket = ticket;
            updateTicketDetails(ticket);
            deleteButton.setDisable(false);
            exportButton.setDisable(false);
        } else {
            System.out.println("Aucun ticket fourni pour pré-remplissage");
            showMessage("Aucun ticket sélectionné", false);
            clearTicketDetails();
        }
    }

    @FXML
    private void deleteTicket() {
        if (selectedTicket == null) {
            showMessage("Aucun ticket à supprimer", false);
            return;
        }

        // Boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le ticket pour " + selectedTicket.getNomPassager() + " ?");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ticketService.delete(selectedTicket.getIdTicket());
                showMessage("Ticket supprimé avec succès", true);
                clearTicketDetails();
                // Fermer la fenêtre après suppression
                rootPane.getScene().getWindow().hide();
            } catch (Exception e) {
                showMessage("Erreur lors de la suppression : " + e.getMessage(), false);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter vers CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Date Emission,Prix,Etat,Nom Passager\n");
                if (selectedTicket != null) {
                    writer.write(String.format("%s,%.3f,%s,%s\n",
                            selectedTicket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            selectedTicket.getPrix(), selectedTicket.getEtatTicket().name(), selectedTicket.getNomPassager()));
                } else {
                    List<Ticket> tickets = ticketService.getAll();
                    for (Ticket ticket : tickets) {
                        writer.write(String.format("%s,%.3f,%s,%s\n",
                                ticket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                ticket.getPrix(), ticket.getEtatTicket().name(), ticket.getNomPassager()));
                    }
                }
                showMessage("Ticket(s) exporté(s) vers CSV", true);
            } catch (IOException e) {
                showMessage("Erreur lors de l'exportation : " + e.getMessage(), false);
                e.printStackTrace();
            } catch (Exception e) {
                showMessage("Erreur lors de l'accès aux données : " + e.getMessage(), false);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void updateTicketDetails(Ticket ticket) {
        System.out.println("Mise à jour des détails pour ticket : " + ticket.toString());
        passengerLabel.setText("Passager: " + ticket.getNomPassager());
        priceLabel.setText(String.format("Prix: %.3f", ticket.getPrix()));
        statusLabel.setText("État: " + ticket.getEtatTicket().name());
        dateLabel.setText("Date: " + ticket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // Charger le QR code
        String qrPath = ticket.getCodeQR();
        System.out.println("Chemin du QR code brut : " + qrPath);
        String adjustedQrPath = null;
        File qrFile = null;

        if (qrPath != null && !qrPath.isEmpty()) {
            // Format 1 : qr_codes/qr_ticket_<idTicket>.png (format utilisé par generateQRCode)
            adjustedQrPath = "qr_codes/qr_ticket_" + ticket.getIdTicket() + ".png";
            qrFile = new File(adjustedQrPath);
            if (!qrFile.exists()) {
                // Format 2 : qr_codes/QR-TICKET-<id>.png
                adjustedQrPath = "qr_codes/" + qrPath;
                if (!adjustedQrPath.endsWith(".png")) {
                    adjustedQrPath += ".png";
                }
                qrFile = new File(adjustedQrPath);
            }
            if (!qrFile.exists()) {
                // Tenter de régénérer le QR code
                try {
                    adjustedQrPath = ticketService.generateQRCode(ticket.getIdTicket());
                    qrFile = new File(adjustedQrPath);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la régénération du QR code : " + e.getMessage());
                }
            }
        }

        System.out.println("Chemin du QR code ajusté : " + adjustedQrPath);
        if (adjustedQrPath != null && qrFile.exists() && qrFile.isFile()) {
            qrCodeImage.setImage(new Image(qrFile.toURI().toString()));
            System.out.println("QR code chargé avec succès : " + adjustedQrPath);
        } else {
            qrCodeImage.setImage(null);
            showMessage("Impossible de charger le QR code : fichier introuvable", false);
            System.out.println("Fichier QR introuvable : " + adjustedQrPath);
        }

        // Animer le panneau des détails
        FadeTransition fade = new FadeTransition(Duration.millis(300), passengerLabel.getParent());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void clearTicketDetails() {
        passengerLabel.setText("Passager: ");
        priceLabel.setText("Prix: ");
        statusLabel.setText("État: ");
        dateLabel.setText("Date: ");
        qrCodeImage.setImage(null);
        deleteButton.setDisable(true);
        exportButton.setDisable(true);
        selectedTicket = null;
    }

    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setStyle(isSuccess ? "-fx-text-fill: #4CAF50;" : "-fx-text-fill: #F44336;");
        FadeTransition fade = new FadeTransition(Duration.millis(3000), messageLabel);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setDelay(Duration.millis(2000));
        fade.play();
    }
}