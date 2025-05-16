package tn.esprit.sirine.controller;

import tn.esprit.sirine.HelloApplication;
import tn.esprit.sirine.services.UserService;
import tn.esprit.sirine.models.User;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SignUpController {
    public VBox signupBox;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField roleField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleSignup() {
        String role = roleField.getText().toLowerCase().trim();

        // Verify that the role is either "user" or "chauffeur"
        if (!role.equals("user") && !role.equals("chauffeur")) {
            errorLabel.setText("Rôle invalide. Veuillez entrer 'user' ou 'chauffeur'.");
            return;
        }

        User user = new User(0, nomField.getText(), prenomField.getText(), emailField.getText(), passwordField.getText(), role);
        boolean success = userService.addUser(user);

        if (success) {
            errorLabel.setText("Inscription réussie !");
        } else {
            errorLabel.setText("Erreur lors de l'inscription.");
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/login.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();

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
    }

    @FXML
    public void initialize() {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), signupBox);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), signupBox);
        slide.setFromY(-30);
        slide.setToY(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.play();
    }

}
