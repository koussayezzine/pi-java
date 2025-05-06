package com.example.event_match_crud.controller;

import com.example.event_match_crud.services.UserService;
import com.example.event_match_crud.models.User;
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
        User user = new User(0, nomField.getText(), prenomField.getText(), emailField.getText(), passwordField.getText(), roleField.getText());
        boolean success = userService.addUser(user);

        if (success) {
            errorLabel.setText("Inscription réussie !");
        } else {
            errorLabel.setText("Erreur lors de l'inscription.");
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/event_match_crud/login.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root));
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
