package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.sirine.models.EtatTicket;
import tn.esprit.sirine.models.Ticket;
import tn.esprit.sirine.services.TicketService;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class AjouterTicket {

    @FXML private DatePicker datePicker;
    @FXML private TextField timePicker;
    @FXML private Button generateQRCodeBtn;
    @FXML private ImageView qrPreview;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> etatTicketComboBox;
    @FXML private Button addTicketBtn;
    @FXML private Label confirmationMessage;
    @FXML private Label ticketDateLabel;
    @FXML private Label ticketPriceLabel;
    @FXML private Label ticketStatusLabel;
    @FXML private Label passengerNameLabel;
    @FXML private ImageView ticketQRCodePreview;
    @FXML private TextField passengerNameField;
    @FXML private Button btnRetour;

    private final TicketService ticketService;

    public AjouterTicket(Connection connection) {
        this.ticketService = new TicketService(connection);
    }

    @FXML
    public void initialize() {
        etatTicketComboBox.getItems().addAll("VALIDE", "ANNULE", "PENDING");

        // Restrict DatePicker to current date or later
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        generateQRCodeBtn.setOnAction(event -> generateQRCode());
        addTicketBtn.setOnAction(event -> addTicket());
    }

    private void generateQRCode() {
        String passengerName = passengerNameField.getText().trim();
        LocalDate date = datePicker.getValue();
        String time = timePicker.getText().trim();

        System.out.println("Tentative de génération QR temporaire: passengerName=" + passengerName + ", date=" + date + ", time=" + time);

        if (passengerName.isEmpty() || date == null || time.isEmpty()) {
            confirmationMessage.setText("Veuillez remplir le nom, la date et l'heure pour générer un QR code.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: Champs incomplets pour QR temporaire.");
            return;
        }

        // Validate date is not before current date
        if (date.isBefore(LocalDate.now())) {
            confirmationMessage.setText("La date d'émission ne peut pas être antérieure à aujourd'hui.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: Date antérieure à aujourd'hui.");
            return;
        }

        // Créer un contenu unique pour le QR code temporaire
        String qrContent = "TEMP-TICKET:" + passengerName + ":" + date + ":" + time;
        String qrCodeFilePath = ticketService.generateTempQRCode(qrContent);
        System.out.println("Chemin du fichier QR temporaire: " + qrCodeFilePath);

        if (qrCodeFilePath == null) {
            confirmationMessage.setText("Erreur lors de la génération du QR Code.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            qrPreview.setImage(null);
            System.out.println("Erreur: qrCodeFilePath est null.");
            return;
        }

        File qrFile = new File(qrCodeFilePath);
        if (qrFile.exists() && qrFile.isFile()) {
            Image qrImage = new Image("file:" + qrFile.getAbsolutePath(), true);
            qrPreview.setImage(qrImage);
            qrPreview.getParent().requestLayout();
            confirmationMessage.setText("QR Code temporaire généré avec succès.");
            confirmationMessage.setStyle("-fx-text-fill: green;");
            System.out.println("QR Code affiché dans qrPreview: " + qrFile.getAbsolutePath());
        } else {
            qrPreview.setImage(null);
            confirmationMessage.setText("Erreur: Fichier QR introuvable (" + qrCodeFilePath + ").");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: Fichier QR n'existe pas ou n'est pas un fichier: " + qrCodeFilePath);
        }
    }

    private void addTicket() {
        LocalDate date = datePicker.getValue();
        String time = timePicker.getText().trim();
        String status = etatTicketComboBox.getValue();
        String priceText = priceField.getText().trim();
        String passengerName = passengerNameField.getText().trim();

        System.out.println("Ajout ticket: date=" + date + ", time=" + time + ", status=" + status + ", price=" + priceText + ", passengerName=" + passengerName);

        if (date == null) {
            confirmationMessage.setText("Veuillez sélectionner une date.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validate date is not before current date
        if (date.isBefore(LocalDate.now())) {
            confirmationMessage.setText("La date d'émission ne peut pas être antérieure à aujourd'hui.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: Date antérieure à aujourd'hui.");
            return;
        }

        if (time.isEmpty()) {
            confirmationMessage.setText("Veuillez entrer une heure.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        if (status == null) {
            confirmationMessage.setText("Veuillez sélectionner un statut.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        if (priceText.isEmpty()) {
            confirmationMessage.setText("Veuillez entrer un prix.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        if (passengerName.isEmpty()) {
            confirmationMessage.setText("Veuillez entrer un nom de passager.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!Pattern.matches("\\d{2}:\\d{2}", time)) {
            confirmationMessage.setText("Format d'heure invalide (HH:mm).");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        String[] timeParts = time.split(":");
        int hour, minute;
        try {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                confirmationMessage.setText("Heure invalide (00:00 à 23:59).");
                confirmationMessage.setStyle("-fx-text-fill: red;");
                return;
            }
        } catch (NumberFormatException e) {
            confirmationMessage.setText("L'heure doit être numérique (HH:mm).");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                confirmationMessage.setText("Le prix doit être positif.");
                confirmationMessage.setStyle("-fx-text-fill: red;");
                return;
            }
        } catch (NumberFormatException e) {
            confirmationMessage.setText("Le prix doit être un nombre valide.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        String[] nameParts = passengerName.split("\\s+");
        if (nameParts.length < 2) {
            confirmationMessage.setText("Veuillez entrer un nom et un prénom (séparés par un espace).");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            return;
        }
        for (String part : nameParts) {
            if (!Pattern.matches("[a-zA-Z-]{2,}", part)) {
                confirmationMessage.setText("Chaque partie du nom (nom/prénom) doit contenir au moins 2 lettres ou tirets.");
                confirmationMessage.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        LocalDateTime dateTime = date.atTime(hour, minute);

        Ticket ticket = new Ticket(
                dateTime,
                "QR-TICKET-" + System.currentTimeMillis(),
                price,
                EtatTicket.valueOf(status),
                passengerName
        );
        ticketService.add(ticket);

        if (ticket.getIdTicket() > 0) {
            confirmationMessage.setText("Ticket ajouté avec succès ! ID du ticket: " + ticket.getIdTicket());
            confirmationMessage.setStyle("-fx-text-fill: green;");
            updateTicketPreview(ticket);
        } else {
            confirmationMessage.setText("Erreur lors de l'ajout du ticket.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: idTicket non généré.");
        }
    }

    private void updateTicketPreview(Ticket ticket) {
        System.out.println("Mise à jour de l'aperçu pour ticket #" + ticket.getIdTicket());

        ticketDateLabel.setText(ticket.getDateEmission().toLocalDate().toString());
        System.out.println("Date d'émission définie: " + ticket.getDateEmission().toLocalDate());

        ticketPriceLabel.setText(ticket.getPrix() + " €");
        System.out.println("Prix défini: " + ticket.getPrix());

        ticketStatusLabel.setText(ticket.getEtatTicket().name());
        System.out.println("Statut défini: " + ticket.getEtatTicket().name());

        passengerNameLabel.setText(ticket.getNomPassager());
        System.out.println("Nom du passager défini: " + ticket.getNomPassager());

        if (ticket.getIdTicket() <= 0) {
            confirmationMessage.setText("Erreur : ID du ticket non défini.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            ticketQRCodePreview.setImage(null);
            System.out.println("Erreur : idTicket non défini.");
            return;
        }

        String qrCodeFilePath = ticketService.generateQRCode(ticket.getIdTicket());
        System.out.println("Chemin du fichier QR pour idTicket=" + ticket.getIdTicket() + ": " + qrCodeFilePath);

        if (qrCodeFilePath == null) {
            confirmationMessage.setText("Erreur lors de la génération du QR Code pour l'aperçu.");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            ticketQRCodePreview.setImage(null);
            System.out.println("Erreur: qrCodeFilePath est null pour idTicket=" + ticket.getIdTicket());
            return;
        }

        File qrFile = new File(qrCodeFilePath);
        if (qrFile.exists() && qrFile.isFile()) {
            Image qrImage = new Image("file:" + qrFile.getAbsolutePath(), true);
            ticketQRCodePreview.setImage(qrImage);
            ticketQRCodePreview.getParent().requestLayout();
            System.out.println("QR Code affiché dans ticketQRCodePreview: " + qrFile.getAbsolutePath());
        } else {
            ticketQRCodePreview.setImage(null);
            confirmationMessage.setText("Erreur: Fichier QR introuvable pour l'aperçu (" + qrCodeFilePath + ").");
            confirmationMessage.setStyle("-fx-text-fill: red;");
            System.out.println("Erreur: Fichier QR n'existe pas ou n'est pas un fichier: " + qrCodeFilePath);
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}