package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.models.Reservation;
import org.example.service.ReservationService;
import org.example.util.DBconnection;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.Connection;
import java.util.Properties;
import java.util.ResourceBundle;

public class AjouterReservation implements Initializable {

    private ReservationService reservationService;
    private Runnable onReservationAdded;

    @FXML private TextField tfNom;
    @FXML private TextField tfEmail;
    @FXML private TextField tfLieuDepart;
    @FXML private TextField tfLieuArrive;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;

    // Configuration pour l'envoi d'email (à sécuriser dans un fichier de configuration)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "votre.email@gmail.com"; // Remplacez par votre email
    private static final String SENDER_PASSWORD = "votre-mot-de-passe-app"; // Remplacez par votre mot de passe d'application

    public AjouterReservation() {
        Connection conn = DBconnection.getInstance().getConn();
        if (conn == null) {
            System.err.println("Erreur: Connexion à la base de données nulle");
            throw new IllegalStateException("Impossible d'établir une connexion à la base de données");
        }
        reservationService = new ReservationService(conn);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le ComboBox
        cbStatut.setItems(FXCollections.observableArrayList("Confirmée", "En attente", "Annulée"));
        cbStatut.setPromptText("Sélectionner un statut");
        System.out.println("ComboBox cbStatut initialisé avec les options: Confirmée, En attente, Annulée");

        // Vérifier les champs FXML
        StringBuilder nullFields = new StringBuilder();
        if (tfNom == null) nullFields.append("tfNom, ");
        if (tfEmail == null) nullFields.append("tfEmail, ");
        if (tfLieuDepart == null) nullFields.append("tfLieuDepart, ");
        if (tfLieuArrive == null) nullFields.append("tfLieuArrive, ");
        if (cbStatut == null) nullFields.append("cbStatut, ");
        if (btnAjouter == null) nullFields.append("btnAjouter, ");
        if (btnRetour == null) nullFields.append("btnRetour, ");

        if (nullFields.length() > 0) {
            System.err.println("Erreur: Les champs FXML suivants ne sont pas initialisés : " + nullFields.toString());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'initialisation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur d'initialisation de l'interface. Champs manquants : " + nullFields.toString());
            alert.showAndWait();
        }
    }

    public void setOnReservationAdded(Runnable callback) {
        this.onReservationAdded = callback;
    }

    public void setReservationService(ReservationService service) {
        this.reservationService = service;
    }

    @FXML
    public void AjouterReservation(ActionEvent event) {
        // Vérifier que les champs FXML sont initialisés
        if (tfNom == null || tfEmail == null || tfLieuDepart == null || tfLieuArrive == null || cbStatut == null) {
            System.err.println("Erreur: Un ou plusieurs champs FXML sont null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Interface non correctement initialisée.");
            alert.showAndWait();
            return;
        }

        String nom = tfNom.getText().trim();
        String email = tfEmail.getText().trim();
        String lieuDepart = tfLieuDepart.getText().trim();
        String lieuArrive = tfLieuArrive.getText().trim();
        String statut = cbStatut.getValue();

        // Validation des champs vides
        if (nom.isEmpty() || email.isEmpty() || lieuDepart.isEmpty() || lieuArrive.isEmpty() || statut == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs incomplets");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            System.out.println("Erreur: Un ou plusieurs champs sont vides (nom, email, lieuDepart, lieuArrive, statut)");
            return;
        }

        // Validation du nom (nom + prénom)
        if (!nom.matches("^[A-Za-zÀ-ÿ'’-]+ [A-Za-zÀ-ÿ'’-]+$")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nom invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un nom et un prénom (ex. : John Doe).");
            alert.showAndWait();
            System.out.println("Erreur: Format de nom invalide: " + nom);
            return;
        }

        // Validation de l'email (format ***@***.***)
        if (!email.matches("^[A-Za-z0-9.]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("E-mail invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un e-mail valide (ex. : user@domain.com).");
            alert.showAndWait();
            System.out.println("Erreur: Format d'email invalide: " + email);
            return;
        }

        // Ajout
        Reservation reservation = new Reservation(nom, email, lieuDepart, lieuArrive, statut);
        try {
            System.out.println("Tentative d'ajout de la réservation: " + reservation);
            reservationService.add(reservation);
            System.out.println("Réservation ajoutée avec succès: " + reservation);

            // Envoi de l'email de confirmation
            sendConfirmationEmail(nom, email, lieuDepart, lieuArrive, statut);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réservation ajoutée avec succès. Un email de confirmation a été envoyé.");
            alert.showAndWait();

            tfNom.clear();
            tfEmail.clear();
            tfLieuDepart.clear();
            tfLieuArrive.clear();
            cbStatut.setValue(null);

            if (onReservationAdded != null) {
                onReservationAdded.run();
                System.out.println("TableView parent notifié pour rafraîchissement");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout : " + e.getMessage());
            alert.showAndWait();
            System.err.println("Erreur lors de l'ajout de la réservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void retour(ActionEvent event) {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }

    private void sendConfirmationEmail(String nom, String email, String lieuDepart, String lieuArrive, String statut) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("maizarayene9@gmail.com", "lzde salu ktxf ojzw");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("maizarayene9@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Confirmation de votre réservation");
            message.setText("Cher(e) " + nom + ",\n\n" +
                    "Votre réservation a été enregistrée avec succès.\n\n" +
                    "Détails de la réservation :\n" +
                    "Nom : " + nom + "\n" +
                    "Email : " + email + "\n" +
                    "Lieu de départ : " + lieuDepart + "\n" +
                    "Lieu d'arrivée : " + lieuArrive + "\n" +
                    "Statut : " + statut + "\n\n" +
                    "Merci de voyager avec nous !\n" +
                    "Cordialement,\nL'équipe de réservation");

            Transport.send(message);
            System.out.println("Email de confirmation envoyé à : " + email);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation à " + email + ": " + e.getMessage());
            e.printStackTrace();
            // Ne pas bloquer l'ajout si l'email échoue
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText(null);
            alert.setContentText("Réservation ajoutée, mais l'envoi de l'email de confirmation a échoué.");
            alert.showAndWait();
        }
    }
}