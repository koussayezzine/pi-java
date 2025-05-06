package com.example.event_match_crud.controller;

import com.example.event_match_crud.services.UserService;
import com.example.event_match_crud.models.User;
import com.example.event_match_crud.utlis.CurrentSession;
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

        User user = userService.authenticate(email, password);

        if (user != null) {
            CurrentSession.getInstance().setUser(user);

            String role = user.getRole();
            String fxmlPath;

            switch (role.toLowerCase()) {
                case "admin":
                    fxmlPath = "/com/example/event_match_crud/dashboard_admin.fxml";
                    break;
                case "user":
                    fxmlPath = "/com/example/event_match_crud/dashboard_user.fxml";
                    break;
                case "chauffeur":
                    fxmlPath = "/com/example/event_match_crud/dashboard_chauffeur.fxml";
                    break;
                default:
                    showError("Rôle non reconnu !");
                    return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur de chargement de l'interface !");
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/event_match_crud/signup.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root));
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

}
