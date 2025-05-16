package tn.esprit.sirine.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.esprit.sirine.models.Reservation;
import tn.esprit.sirine.services.ReservationService;
import tn.esprit.sirine.utils.MaConnexion;

import java.util.Arrays;
import java.util.List;

public class ModifierReservation {

    @FXML private TextField tfNom;
    @FXML private TextField tfEmail;
    @FXML private ComboBox<String> cbDepart;
    @FXML private ComboBox<String> cbArrivee;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Label messageLabel;
    @FXML private Button btnRechercher;
    @FXML private Button btnRetour;

    private ReservationService reservationService;
    private Reservation reservationTrouvee; // Réservation en cours de modification
    private Runnable onReservationModified; // Méthode de rappel pour rafraîchir le TableView

    public ModifierReservation() {
        this.reservationService = new ReservationService(MaConnexion.getConnection());
    }

    @FXML
    public void initialize() {
        // Vérifier les champs FXML
        StringBuilder nullFields = new StringBuilder();
        if (tfNom == null) nullFields.append("tfNom, ");
        if (tfEmail == null) nullFields.append("tfEmail, ");
        if (cbDepart == null) nullFields.append("cbDepart, ");
        if (cbArrivee == null) nullFields.append("cbArrivee, ");
        if (cbStatut == null) nullFields.append("cbStatut, ");
        if (messageLabel == null) nullFields.append("messageLabel, ");
        if (btnRechercher == null) nullFields.append("btnRechercher, ");
        if (btnRetour == null) nullFields.append("btnRetour, ");

        if (nullFields.length() > 0) {
            System.err.println("Erreur: Les champs FXML suivants ne sont pas initialisés : " + nullFields.toString());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'initialisation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur d'initialisation de l'interface. Champs manquants : " + nullFields.toString());
            alert.showAndWait();
        }

        // Initialiser les ComboBox
        List<String> villes = Arrays.asList(
                "Ariana", "Beja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan",
                "Kasserine", "Kébili", "Mahdia", "Manouba", "Medenine", "Monastir", "Nabeul",
                "Sfax", "Sidi Bouzid", "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan"
        );

        cbDepart.setItems(FXCollections.observableArrayList(villes));
        cbArrivee.setItems(FXCollections.observableArrayList(villes));
        cbStatut.setItems(FXCollections.observableArrayList("Confirmée", "En attente", "Annulée"));
        System.out.println("ComboBox initialisés : cbDepart, cbArrivee, cbStatut");
    }

    // Définir la méthode de rappel
    public void setOnReservationModified(Runnable callback) {
        this.onReservationModified = callback;
    }

    // Permettre de passer un ReservationService externe (optionnel)
    public void setReservationService(ReservationService service) {
        this.reservationService = service;
    }

    // Pré-remplir les champs avec la réservation sélectionnée
    public void setReservationToEdit(Reservation reservation) {
        this.reservationTrouvee = reservation;
        tfNom.setText(reservation.getNomUtilisateur());
        tfEmail.setText(reservation.getEmail());
        cbDepart.setValue(reservation.getLieuDepart());
        cbArrivee.setValue(reservation.getLieuArrive());
        cbStatut.setValue(reservation.getStatut());
        messageLabel.setText("");
        // Désactiver le bouton de recherche car la réservation est déjà sélectionnée
        btnRechercher.setDisable(true);
    }

    @FXML
    private void rechercherReservation() {
        String nom = tfNom.getText().trim();
        if (nom.isEmpty()) {
            afficherErreur("Veuillez entrer un nom pour rechercher.");
            return;
        }

        reservationTrouvee = reservationService.getByNom(nom);
        if (reservationTrouvee == null) {
            afficherErreur("Aucune réservation trouvée pour ce nom.");
        } else {
            tfEmail.setText(reservationTrouvee.getEmail());
            cbDepart.setValue(reservationTrouvee.getLieuDepart());
            cbArrivee.setValue(reservationTrouvee.getLieuArrive());
            cbStatut.setValue(reservationTrouvee.getStatut());
            messageLabel.setText("Réservation trouvée.");
            messageLabel.setTextFill(Color.GREEN);
        }
    }

    @FXML
    private void modifierReservation() {
        if (reservationTrouvee == null) {
            afficherErreur("Aucune réservation sélectionnée à modifier.");
            return;
        }

        // Mise à jour des champs
        String nom = tfNom.getText().trim();
        String email = tfEmail.getText().trim();
        String depart = cbDepart.getValue();
        String arrivee = cbArrivee.getValue();
        String statut = cbStatut.getValue();

        // Validation des champs vides
        if (nom.isEmpty() || email.isEmpty() || depart == null || arrivee == null || statut == null) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        // Validation du nom (nom + prénom)
        if (!nom.matches("^[A-Za-zÀ-ÿ'’-]+ [A-Za-zÀ-ÿ'’-]+$")) {
            afficherErreur("Veuillez entrer un nom et un prénom (ex. : John Doe).");
            System.out.println("Erreur: Format de nom invalide: " + nom);
            return;
        }

        // Validation de l'email (format ***@***.***)
        if (!email.matches("^[A-Za-z0-9.]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
            afficherErreur("Veuillez entrer un e-mail valide (ex. : user@domain.com).");
            System.out.println("Erreur: Format d'email invalide: " + email);
            return;
        }

        try {
            // Mettre à jour la réservation
            reservationTrouvee.setNomUtilisateur(nom);
            reservationTrouvee.setEmail(email);
            reservationTrouvee.setLieuDepart(depart);
            reservationTrouvee.setLieuArrive(arrivee);
            reservationTrouvee.setStatut(statut);

            reservationService.update(reservationTrouvee);

            // Afficher le message de succès
            messageLabel.setText("Réservation modifiée avec succès.");
            messageLabel.setTextFill(Color.GREEN);

            // Réinitialiser les champs pour permettre une autre modification
            tfNom.clear();
            tfEmail.clear();
            cbDepart.setValue(null);
            cbArrivee.setValue(null);
            cbStatut.setValue(null);
            reservationTrouvee = null;
            btnRechercher.setDisable(false);

            // Notifier le parent pour rafraîchir le TableView
            if (onReservationModified != null) {
                onReservationModified.run();
                System.out.println("TableView parent notifié pour rafraîchissement");
            }
        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerModification() {
        tfNom.clear();
        tfEmail.clear();
        cbDepart.setValue(null);
        cbArrivee.setValue(null);
        cbStatut.setValue(null);
        messageLabel.setText("");
        reservationTrouvee = null;
        btnRechercher.setDisable(false);
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String message) {
        messageLabel.setText(message);
        messageLabel.setTextFill(Color.RED);
    }

    private void afficherErreur(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setTextFill(color);
    }
}