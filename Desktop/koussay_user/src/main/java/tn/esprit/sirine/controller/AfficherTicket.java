package tn.esprit.sirine.controller;

import jakarta.mail.AuthenticationFailedException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.sirine.services.TicketService;
import tn.esprit.sirine.models.Ticket;
import tn.esprit.sirine.utils.MaConnexion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class AfficherTicket {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> ticketComboBox;
    @FXML private Label labelEtat;
    @FXML private Label labelDateEmission;
    @FXML private Label labelPrix;
    @FXML private ImageView imageQRCode;
    @FXML private Label labelNote;
    @FXML private Label labelError;
    @FXML private Button exportPdfButton;
    @FXML private TextField emailField;
    @FXML private Button sendEmailButton;
    @FXML private Button btnRetour;

    private TicketService ticketService;
    private List<Ticket> foundTickets;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public AfficherTicket() {
        Connection conn = MaConnexion.getConnection();
        ticketService = new TicketService(conn);
    }

    @FXML
    public void initialize() {
        labelEtat.setText("N/A");
        labelNote.setText("Merci pour votre réservation.");

        ticketComboBox.setOnAction(event -> {
            String selected = ticketComboBox.getSelectionModel().getSelectedItem();
            System.out.println("ComboBox sélectionné: " + selected);
            if (selected != null) {
                try {
                    String idStr = selected.split(" - ")[0].replace("Ticket #", "");
                    int ticketId = Integer.parseInt(idStr);
                    System.out.println("Ticket ID extrait: " + ticketId);
                    displayTicketDetails(ticketId);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    labelError.setText("Erreur lors de la sélection du ticket.");
                    System.out.println("Erreur lors du parsing de ticketId: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void setSelectedTicket(Ticket ticket) {
        System.out.println("setSelectedTicket appelé avec ticket: " + (ticket != null ? ticket.getIdTicket() : "null"));
        if (ticket != null) {
            searchField.setDisable(true);
            searchButton.setDisable(true);
            ticketComboBox.setVisible(false);
            foundTickets = List.of(ticket);
            displayTicketDetails(ticket.getIdTicket());
        } else {
            labelError.setText("Aucun ticket sélectionné.");
            clearTicketDetails();
        }
    }

    @FXML
    private void rechercheticket() {
        String nomPassager = searchField.getText().trim();
        System.out.println("Recherche pour nomPassager: " + nomPassager);

        if (nomPassager.isEmpty()) {
            labelError.setText("Veuillez entrer un nom de passager.");
            labelNote.setText("");
            ticketComboBox.setVisible(false);
            clearTicketDetails();
            System.out.println("Erreur: Champ de recherche vide.");
            return;
        }

        try {
            foundTickets = ticketService.getAllByNomPassager(nomPassager);
            System.out.println("Tickets trouvés pour " + nomPassager + ": " + foundTickets.size());

            if (foundTickets.isEmpty()) {
                labelError.setText("Aucun ticket trouvé pour ce passager.");
                labelNote.setText("");
                ticketComboBox.setVisible(false);
                clearTicketDetails();
                System.out.println("Aucun ticket trouvé.");
                return;
            }

            labelError.setText("");
            labelNote.setText("Tickets trouvés : " + foundTickets.size());

            if (foundTickets.size() == 1) {
                ticketComboBox.setVisible(false);
                displayTicketDetails(foundTickets.get(0).getIdTicket());
                System.out.println("Affichage direct du ticket #" + foundTickets.get(0).getIdTicket());
            } else {
                ticketComboBox.getItems().clear();
                for (Ticket ticket : foundTickets) {
                    String item = "Ticket #" + ticket.getIdTicket() + " - " +
                            ticket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    ticketComboBox.getItems().add(item);
                    System.out.println("Ajout au ComboBox: " + item);
                }
                ticketComboBox.setVisible(true);
                ticketComboBox.getSelectionModel().selectFirst();
                displayTicketDetails(foundTickets.get(0).getIdTicket());
                System.out.println("ComboBox rempli, premier ticket affiché: #" + foundTickets.get(0).getIdTicket());
            }
        } catch (Exception e) {
            labelError.setText("Erreur lors de la recherche des tickets.");
            labelNote.setText("");
            ticketComboBox.setVisible(false);
            clearTicketDetails();
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportTicketToPdf() {
        System.out.println("Exportation du ticket en PDF");

        final int ticketId;
        String selected = ticketComboBox.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                String idStr = selected.split(" - ")[0].replace("Ticket #", "");
                ticketId = Integer.parseInt(idStr);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                labelError.setText("Erreur lors de la sélection du ticket.");
                System.out.println("Erreur lors du parsing de ticketId pour PDF: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else if (foundTickets != null && foundTickets.size() == 1) {
            ticketId = foundTickets.get(0).getIdTicket();
        } else {
            labelError.setText("Veuillez sélectionner un ticket.");
            System.out.println("Erreur: Aucun ticket sélectionné.");
            return;
        }

        Ticket ticket = foundTickets.stream()
                .filter(t -> t.getIdTicket() == ticketId)
                .findFirst()
                .orElse(null);

        if (ticket == null) {
            labelError.setText("Ticket non trouvé.");
            System.out.println("Erreur: Ticket #" + ticketId + " non trouvé.");
            return;
        }

        try {
            String pdfPath = generateTicketPDF(ticket);
            if (pdfPath == null) {
                labelError.setText("Erreur lors de la génération du PDF.");
                System.out.println("Erreur: Échec de la génération du PDF.");
                return;
            }

            labelError.setText("");
            labelNote.setText("PDF exporté avec succès : " + pdfPath);
            System.out.println("PDF exporté: " + pdfPath);
        } catch (Exception e) {
            labelError.setText("Erreur lors de l'exportation du PDF.");
            System.out.println("Erreur lors de l'exportation du PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void sendTicketByEmail() {
        System.out.println("Tentative d'envoi du ticket par e-mail");

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            labelError.setText("Veuillez entrer une adresse e-mail.");
            labelNote.setText("");
            System.out.println("Erreur: Champ e-mail vide.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            labelError.setText("Adresse e-mail invalide.");
            labelNote.setText("");
            System.out.println("Erreur: E-mail invalide: " + email);
            return;
        }

        final int ticketId;
        String selected = ticketComboBox.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                String idStr = selected.split(" - ")[0].replace("Ticket #", "");
                ticketId = Integer.parseInt(idStr);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                labelError.setText("Erreur lors de la sélection du ticket.");
                System.out.println("Erreur lors du parsing de ticketId pour e-mail: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else if (foundTickets != null && foundTickets.size() == 1) {
            ticketId = foundTickets.get(0).getIdTicket();
        } else {
            labelError.setText("Veuillez sélectionner un ticket.");
            System.out.println("Erreur: Aucun ticket sélectionné pour l'envoi par e-mail.");
            return;
        }

        Ticket ticket = foundTickets.stream()
                .filter(t -> t.getIdTicket() == ticketId)
                .findFirst()
                .orElse(null);

        if (ticket == null) {
            labelError.setText("Ticket non trouvé.");
            System.out.println("Erreur: Ticket #" + ticketId + " non trouvé.");
            return;
        }

        try {
            String pdfPath = generateTicketPDF(ticket);
            if (pdfPath == null) {
                labelError.setText("Erreur lors de la génération du PDF.");
                System.out.println("Erreur: Échec de la génération du PDF pour l'envoi.");
                return;
            }

            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                labelError.setText("Le fichier PDF n'existe pas.");
                System.out.println("Erreur: Fichier PDF introuvable à " + pdfPath);
                return;
            }

            System.out.println("Tentative d'envoi du ticket par e-mail à: " + email);
            sendEmailWithAttachment(email, ticket, pdfPath);

            labelError.setText("");
            labelNote.setText("Ticket envoyé par e-mail avec succès.");
            System.out.println("E-mail envoyé avec succès à " + email);
        } catch (MessagingException e) {
            labelError.setText("Échec de l'authentification e-mail. Vérifiez l'adresse e-mail et le mot de passe de l'application.");
            System.out.println("Erreur d'authentification SMTP: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            labelError.setText("Erreur inattendue lors de l'envoi de l'e-mail.");
            System.out.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendEmailWithAttachment(String recipientEmail, Ticket ticket, String pdfPath) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", "true");

        final String username = "maizarayene9@gmail.com";
        final String password = "lzde salu ktxf ojzw";

        System.out.println("Tentative d'authentification SMTP avec l'utilisateur: " + username);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Votre Ticket de Réservation #" + ticket.getIdTicket());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Bonjour,\n\nVeuillez trouver ci-joint votre ticket de réservation.\n\nCordialement,\nÉquipe de Gestion des Tickets");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        File pdfFile = new File(pdfPath);
        System.out.println("Vérification du fichier PDF : " + pdfPath + ", Existe : " + pdfFile.exists());
        try {
            attachmentPart.attachFile(pdfFile);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'attachement du fichier PDF : " + e.getMessage());
            throw new MessagingException("Impossible d'attacher le fichier PDF", e);
        }

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private String generateTicketPDF(Ticket ticket) {
        String pdfPath = System.getProperty("user.dir") + "/tickets/ticket_" + ticket.getIdTicket() + ".pdf";
        System.out.println("Tentative de génération du PDF à : " + pdfPath);
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Ticket de Réservation");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("ID Ticket: #" + ticket.getIdTicket());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Nom du Passager: " + ticket.getNomPassager());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date d’émission: " +
                        ticket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Prix: " + ticket.getPrix() + " TND");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("État: " + ticket.getEtatTicket().name());
                contentStream.endText();

                String qrCodePath = "qr_codes/qr_ticket_" + ticket.getIdTicket() + ".png";
                File qrCodeFile = new File(qrCodePath);
                if (!qrCodeFile.exists()) {
                    qrCodePath = ticketService.generateQRCode(ticket.getIdTicket());
                    qrCodeFile = new File(qrCodePath);
                }
                System.out.println("Chemin du QR code pour PDF : " + qrCodePath);
                if (qrCodePath != null && qrCodeFile.exists()) {
                    PDImageXObject pdImage = PDImageXObject.createFromFile(qrCodePath, document);
                    contentStream.drawImage(pdImage, 400, 600, 150, 150);
                } else {
                    System.out.println("Avertissement : QR code non généré ou introuvable à " + qrCodePath);
                }
            }

            File pdfFile = new File(pdfPath);
            if (!pdfFile.getParentFile().exists()) {
                pdfFile.getParentFile().mkdirs();
                System.out.println("Dossier créé : " + pdfFile.getParent());
            }
            document.save(pdfFile);
            System.out.println("PDF généré avec succès à : " + pdfPath);
            return pdfPath;
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void displayTicketDetails(int ticketId) {
        System.out.println("Affichage des détails pour ticket #" + ticketId);
        Ticket ticket = foundTickets.stream()
                .filter(t -> t.getIdTicket() == ticketId)
                .findFirst()
                .orElse(null);

        if (ticket == null) {
            labelError.setText("Ticket non trouvé.");
            clearTicketDetails();
            System.out.println("Erreur: Ticket #" + ticketId + " non trouvé dans foundTickets.");
            return;
        }

        labelDateEmission.setText("Date d’émission: " +
                ticket.getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        labelPrix.setText(String.format("Prix: %.3f TND", ticket.getPrix()));
        labelEtat.setText(ticket.getEtatTicket().name());
        System.out.println("Détails mis à jour: ID=" + ticket.getIdTicket() + ", Date=" +
                ticket.getDateEmission() + ", Prix=" + ticket.getPrix() + ", État=" + ticket.getEtatTicket());

        switch (ticket.getEtatTicket()) {
            case VALIDE:
                labelEtat.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 10;");
                break;
            case ANNULE:
                labelEtat.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 10;");
                break;
            case PENDING:
                labelEtat.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 10;");
                break;
        }

        try {
            String qrCodePath = "qr_codes/qr_ticket_" + ticketId + ".png";
            File qrCodeFile = new File(qrCodePath);
            if (!qrCodeFile.exists()) {
                qrCodePath = ticketService.generateQRCode(ticketId);
                qrCodeFile = new File(qrCodePath);
            }
            System.out.println("Génération QR pour ticket #" + ticketId + ": " + qrCodePath);
            if (qrCodePath != null && qrCodeFile.exists()) {
                Image qrCodeImage = new Image("file:" + qrCodePath, true);
                imageQRCode.setImage(qrCodeImage);
                System.out.println("QR Code affiché: " + qrCodePath);
            } else {
                imageQRCode.setImage(null);
                labelError.setText("Erreur: Fichier QR introuvable.");
                System.out.println("Erreur: Fichier QR introuvable: " + qrCodePath);
            }
        } catch (Exception e) {
            imageQRCode.setImage(null);
            labelError.setText("Erreur lors de l'affichage du QR Code.");
            System.out.println("Erreur lors de l'affichage du QR Code pour ticket #" + ticketId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearTicketDetails() {
        labelDateEmission.setText("Date d’émission: N/A");
        labelPrix.setText("Prix: N/A");
        labelEtat.setText("N/A");
        labelEtat.setStyle("-fx-background-color: transparent; -fx-text-fill: #34495e;");
        imageQRCode.setImage(null);
        System.out.println("Détails réinitialisés.");
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }
}