package tn.esprit.sirine.controller;

import tn.esprit.sirine.HelloApplication;
import tn.esprit.sirine.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class ForgotPasswordController {
    @FXML
    private TextField emailField;

    private final UserService userService = new UserService();

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();

        // Validate email
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer votre e-mail.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer une adresse e-mail valide.");
            return;
        }

        // Show processing message
        System.out.println("Processing password reset for: " + email);

        // Call service to reset password
        String result = userService.resetPassword(email);

        // Determine alert type based on result
        Alert.AlertType alertType = result.contains("Erreur") || result.contains("Aucun")
            ? Alert.AlertType.ERROR
            : Alert.AlertType.INFORMATION;

        showAlert(alertType, result);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.INFORMATION ? "Succès" : "Erreur");
        alert.setHeaderText(type == Alert.AlertType.INFORMATION ? "Réinitialisation du mot de passe" : "Problème de réinitialisation");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturnToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Configure the stage
            stage.setTitle(HelloApplication.APP_NAME + " - Connexion");

            // Create and configure the scene
            Scene scene = new Scene(root);

            // Apply CSS directly
            try {
                scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/components.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/light-theme.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("Could not apply CSS: " + e.getMessage());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour à la page de connexion.");
        }
    }
}
