package tn.esprit.sirine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.sirine.models.EtatTicket;
import tn.esprit.sirine.models.Ticket;
import tn.esprit.sirine.services.TicketService;
import tn.esprit.sirine.utils.MaConnexion;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ModifierTicket {

    @FXML private TextField idTicketField;
    @FXML private Button btnChargerTicket;
    @FXML private DatePicker dateEmissionPicker;
    @FXML private TextField timeField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private ImageView qrImageView;
    @FXML private Label qrCodeLabel;
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnRetour;
    @FXML private Label messageLabel;

    private TicketService ticketService;
    private Ticket currentTicket;

    public ModifierTicket() {
        Connection conn = MaConnexion.getConnection();
        this.ticketService = new TicketService(conn);
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation de ModifierTicket");
        etatComboBox.getItems().addAll("VALIDE", "ANNULE", "PENDING");

        // Restrict DatePicker to current date or later
        dateEmissionPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        btnChargerTicket.setOnAction(event -> chargerTicket());
        btnModifier.setOnAction(event -> modifierTicket());
        btnAnnuler.setOnAction(event -> annuler());
    }

    public void setTicket(Ticket ticket) {
        System.out.println("setTicket appelé avec ticket : " + (ticket != null ? ticket.toString() : "null"));
        this.currentTicket = ticket;
        if (ticket != null) {
            populateFields();
        } else {
            System.out.println("Aucun ticket fourni pour pré-remplissage");
        }
    }

    private void populateFields() {
        System.out.println("Remplissage des champs pour ticket : " + (currentTicket != null ? currentTicket.toString() : "null"));
        if (currentTicket == null) {
            messageLabel.setText("Aucun ticket à afficher.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            idTicketField.setText(currentTicket.getNomPassager());
            idTicketField.setEditable(false);

            dateEmissionPicker.setValue(currentTicket.getDateEmission().toLocalDate());
            timeField.setText(String.format("%02d:%02d",
                    currentTicket.getDateEmission().getHour(),
                    currentTicket.getDateEmission().getMinute()));
            prixField.setText(String.valueOf(currentTicket.getPrix()));
            etatComboBox.setValue(currentTicket.getEtatTicket().name());

            String qrCodePath = ticketService.generateQRCode(currentTicket.getIdTicket());
            System.out.println("Chemin du QR code : " + qrCodePath);
            if (qrCodePath != null) {
                qrImageView.setImage(new Image("file:" + qrCodePath));
                qrCodeLabel.setText("QR Code pour Ticket #" + currentTicket.getIdTicket());
            } else {
                qrImageView.setImage(null);
                qrCodeLabel.setText("Erreur lors de la génération du QR Code.");
            }

            messageLabel.setText("Ticket chargé avec succès : " + currentTicket.getNomPassager());
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            System.err.println("Erreur dans populateFields : " + e.getMessage());
            messageLabel.setText("Erreur lors du chargement des données du ticket : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void chargerTicket() {
        String nomPassager = idTicketField.getText().trim();
        if (nomPassager.isEmpty()) {
            messageLabel.setText("Veuillez entrer un nom de passager.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            List<Ticket> tickets = ticketService.getAllByNomPassager(nomPassager);
            if (tickets.isEmpty()) {
                messageLabel.setText("Aucun ticket trouvé pour le passager : " + nomPassager);
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (tickets.size() > 1) {
                ChoiceDialog<Integer> dialog = new ChoiceDialog<>(null, tickets.stream().map(Ticket::getIdTicket).toList());
                dialog.setTitle("Plusieurs tickets trouvés");
                dialog.setHeaderText("Plusieurs tickets existent pour : " + nomPassager);
                dialog.setContentText("Sélectionnez l'ID du ticket à modifier :");
                dialog.showAndWait().ifPresent(id -> {
                    currentTicket = tickets.stream().filter(t -> t.getIdTicket() == id).findFirst().orElse(null);
                    if (currentTicket != null) {
                        populateFields();
                    }
                });
            } else {
                currentTicket = tickets.get(0);
                populateFields();
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement du ticket : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void modifierTicket() {
        if (currentTicket == null) {
            messageLabel.setText("Aucun ticket chargé.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        LocalDate date = dateEmissionPicker.getValue();
        String time = timeField.getText().trim();
        String prixText = prixField.getText().trim();
        String etat = etatComboBox.getValue();

        if (date == null || time.isEmpty() || prixText.isEmpty() || etat == null) {
            messageLabel.setText("Tous les champs doivent être remplis.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validate date is not before current date
        if (date.isBefore(LocalDate.now())) {
            messageLabel.setText("La date d'émission ne peut pas être antérieure à aujourd'hui.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de modification");
        confirmationAlert.setHeaderText("Modifier le ticket");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir modifier ce ticket ?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String[] timeParts = time.split(":");
                    if (timeParts.length != 2) {
                        messageLabel.setText("Format d'heure invalide (HH:mm).");
                        messageLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    LocalDateTime dateTime = date.atTime(hour, minute);

                    double prix = Double.parseDouble(prixText);

                    currentTicket.setDateEmission(dateTime);
                    currentTicket.setPrix(prix);
                    currentTicket.setEtatTicket(EtatTicket.valueOf(etat));
                    ticketService.update(currentTicket);

                    String qrCodePath = ticketService.generateQRCode(currentTicket.getIdTicket());
                    if (qrCodePath != null) {
                        qrImageView.setImage(new Image("file:" + qrCodePath));
                    }

                    Alert successAlert = new Alert(AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText("Ticket modifié");
                    successAlert.setContentText("Le ticket a été modifié avec succès !");
                    successAlert.showAndWait();

                    messageLabel.setText("Ticket modifié avec succès.");
                    messageLabel.setStyle("-fx-text-fill: green;");
                } catch (NumberFormatException e) {
                    messageLabel.setText("Le prix ou l'heure est invalide.");
                    messageLabel.setStyle("-fx-text-fill: red;");
                } catch (Exception e) {
                    messageLabel.setText("Erreur lors de la modification du ticket.");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    e.printStackTrace();
                }
            }
        });
    }

    private void annuler() {
        idTicketField.clear();
        idTicketField.setEditable(true);
        dateEmissionPicker.setValue(null);
        timeField.clear();
        prixField.clear();
        etatComboBox.setValue(null);
        qrImageView.setImage(null);
        qrCodeLabel.setText("QR Code");
        messageLabel.setText("");
        currentTicket = null;
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}