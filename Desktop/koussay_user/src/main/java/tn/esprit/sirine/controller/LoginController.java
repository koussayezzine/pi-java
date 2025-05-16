package tn.esprit.sirine.controller;

import tn.esprit.sirine.HelloApplication;
import tn.esprit.sirine.services.UserService;
import tn.esprit.sirine.models.User;
import tn.esprit.sirine.utils.CurrentSession;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    public VBox loginBox;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Attempting login with email: " + email);

        User user = userService.authenticate(email, password);

        if (user != null) {
            System.out.println("Authentication successful for user: " + user.getUsername());
            System.out.println("User role: " + user.getRole());
            CurrentSession.getInstance().setUser(user);

            String role = user.getRole();
            String fxmlPath;

            switch (role.toLowerCase()) {
                case "admin":
                    System.out.println("Redirecting to admin dashboard");
                    fxmlPath = "/tn/esprit/sirine/dashboard_admin.fxml";
                    break;
                case "user":
                    System.out.println("Redirecting to user dashboard");
                    fxmlPath = "/tn/esprit/sirine/dashboard_user.fxml";
                    break;
                case "chauffeur":
                    System.out.println("Redirecting to chauffeur dashboard");
                    fxmlPath = "/tn/esprit/sirine/dashboard_chauffeur.fxml";
                    break;
                default:
                    System.out.println("Unrecognized role: " + role);
                    showError("Rôle non reconnu !");
                    return;
            }

            try {
                System.out.println("Loading FXML from path: " + fxmlPath);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                if (loader.getLocation() == null) {
                    System.err.println("FXML location is null! Path: " + fxmlPath);
                    showError("Erreur: Fichier FXML introuvable!");
                    return;
                }

                System.out.println("FXML location: " + loader.getLocation());
                Parent root = loader.load();
                System.out.println("FXML loaded successfully");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Configure the stage
                stage.setTitle(HelloApplication.APP_NAME + " - " + role.substring(0, 1).toUpperCase() + role.substring(1));

                // Create and configure the scene
                Scene scene = new Scene(root);

                // Apply CSS directly
                try {
                    scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/components.css").toExternalForm());
                    scene.getStylesheets().add(getClass().getResource("/tn/esprit/sirine/light-theme.css").toExternalForm());
                    System.out.println("CSS applied successfully");
                } catch (Exception e) {
                    System.err.println("Could not apply CSS: " + e.getMessage());
                    e.printStackTrace();
                }

                stage.setScene(scene);
                stage.show();
                System.out.println("Dashboard displayed successfully");
            } catch (IOException e) {
                System.err.println("Error loading interface: " + e.getMessage());
                e.printStackTrace();
                showError("Erreur de chargement de l'interface !");
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                showError("Erreur inattendue: " + e.getMessage());
            }
        } else {
            showError("Email ou mot de passe incorrect !");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    @FXML
    private void switchToSignup() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/signup.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();

        // Configure the stage
        stage.setTitle(HelloApplication.APP_NAME + " - Inscription");

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
        // Animation d'apparition du VBox
        FadeTransition fade = new FadeTransition(Duration.seconds(1), loginBox);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), loginBox);
        slide.setFromY(-30);
        slide.setToY(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.play();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/tn/esprit/sirine/forgot_password.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();

        // Configure the stage
        stage.setTitle(HelloApplication.APP_NAME + " - Récupération de mot de passe");

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

}
